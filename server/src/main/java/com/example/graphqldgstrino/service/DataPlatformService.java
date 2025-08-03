package com.example.graphqldgstrino.service;

import com.example.graphqldgstrino.model.dataplatform.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据中台核心服务类
 * 提供通用的表查询、元数据管理、权限控制、异步查询等功能
 */
@Service
public class DataPlatformService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private MetadataService metadataService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private QueryCacheService cacheService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 异步查询任务管理
    private final Map<String, AsyncQueryTask> queryTasks = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<QueryResult>> runningQueries = new ConcurrentHashMap<>();
    
    /**
     * 通用表查询 - 核心查询接口
     */
    public TableQueryResult queryByTable(String tableName, TableFilter filter, 
                                        PaginationInput pagination, List<String> fieldSelection,
                                        List<OrderByInput> orderBy, String userId) {
        
        // 1. 权限检查
        UserPermissions permissions = permissionService.getUserPermissions(userId, tableName);
        if (!permissionService.hasTablePermission(permissions, tableName, "SELECT")) {
            throw new SecurityException("用户无权限访问表: " + tableName);
        }
        
        // 2. 字段权限过滤
        List<String> allowedFields = permissionService.filterAllowedFields(permissions, tableName, fieldSelection);
        
        // 3. 构建查询缓存键
        String cacheKey = buildCacheKey(tableName, filter, pagination, allowedFields, orderBy);
        
        // 4. 检查缓存
        TableQueryResult cachedResult = cacheService.getQueryResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 5. 构建SQL查询
        long startTime = System.currentTimeMillis();
        String sql = buildDynamicQuery(tableName, filter, pagination, allowedFields, orderBy, permissions);
        
        try {
            // 6. 执行查询
            List<Map<String, Object>> rawData = jdbcTemplate.queryForList(sql);
            
            // 7. 数据脱敏处理
            List<Map<String, Object>> maskedData = permissionService.applyDataMasking(rawData, permissions, tableName);
            
            // 8. 获取总数（如果需要分页）
            int totalCount = getTotalCount(tableName, filter, permissions);
            
            // 9. 构建结果
            long executionTime = System.currentTimeMillis() - startTime;
            QueryMetadata metadata = buildQueryMetadata(sql, executionTime);
            
            TableQueryResult result = TableQueryResult.builder()
                .data(maskedData)
                .totalCount(totalCount)
                .hasNextPage(hasNextPage(pagination, totalCount))
                .executionTime((int) executionTime)
                .fromCache(false)
                .metadata(metadata)
                .build();
            
            // 10. 缓存结果
            cacheService.cacheQueryResult(cacheKey, result);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("查询执行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 提交异步查询
     */
    public AsyncQueryTask submitAsyncQuery(AsyncQueryInput input, String userId) {
        // 1. 权限检查
        if (input.getTableName() != null) {
            UserPermissions permissions = permissionService.getUserPermissions(userId, input.getTableName());
            if (!permissionService.hasTablePermission(permissions, input.getTableName(), "SELECT")) {
                throw new SecurityException("用户无权限访问表: " + input.getTableName());
            }
        }
        
        // 2. 创建查询任务
        String taskId = UUID.randomUUID().toString();
        AsyncQueryTask task = AsyncQueryTask.builder()
            .taskId(taskId)
            .status(QueryStatus.SUBMITTED)
            .submittedAt(LocalDateTime.now())
            .priority(input.getPriority())
            .resourceGroup(determineResourceGroup(input, userId))
            .estimatedDuration(estimateQueryDuration(input))
            .build();
        
        queryTasks.put(taskId, task);
        
        // 3. 异步执行查询
        CompletableFuture<QueryResult> future = CompletableFuture.supplyAsync(() -> {
            try {
                task.setStatus(QueryStatus.RUNNING);
                return executeAsyncQuery(input, userId, taskId);
            } catch (Exception e) {
                task.setStatus(QueryStatus.FAILED);
                throw new RuntimeException(e);
            }
        });
        
        runningQueries.put(taskId, future);
        
        return task;
    }
    
    /**
     * 获取查询任务状态
     */
    public QueryTaskStatus getQueryTaskStatus(String taskId) {
        AsyncQueryTask task = queryTasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("查询任务不存在: " + taskId);
        }
        
        CompletableFuture<QueryResult> future = runningQueries.get(taskId);
        float progress = calculateProgress(task, future);
        
        return QueryTaskStatus.builder()
            .taskId(taskId)
            .status(task.getStatus())
            .progress(progress)
            .submittedAt(task.getSubmittedAt())
            .startedAt(task.getStartedAt())
            .completedAt(task.getCompletedAt())
            .executionTime(task.getExecutionTime())
            .build();
    }
    
    /**
     * 获取查询结果
     */
    public QueryResult getQueryResult(String taskId, PaginationInput pagination) {
        CompletableFuture<QueryResult> future = runningQueries.get(taskId);
        if (future == null) {
            throw new IllegalArgumentException("查询任务不存在: " + taskId);
        }
        
        try {
            QueryResult result = future.get();
            
            // 应用分页
            if (pagination != null) {
                return applyPagination(result, pagination);
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取查询结果失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取表元数据
     */
    @Cacheable(value = "tableMetadata", key = "#tableName")
    public TableMetadata getTableMetadata(String tableName) {
        return metadataService.getTableMetadata(tableName);
    }
    
    /**
     * 列出表信息
     */
    public List<TableInfo> listTables(TableListFilter filter, String userId) {
        // 获取用户可访问的表列表
        UserPermissions permissions = permissionService.getUserPermissions(userId, null);
        List<String> accessibleTables = permissionService.getAccessibleTables(permissions);
        
        return metadataService.listTables(filter)
            .stream()
            .filter(table -> accessibleTables.contains(table.getTableName()))
            .collect(Collectors.toList());
    }
    
    /**
     * 获取表结构
     */
    @Cacheable(value = "tableSchema", key = "#tableName")
    public TableSchema getTableSchema(String tableName, String userId) {
        // 权限检查
        UserPermissions permissions = permissionService.getUserPermissions(userId, tableName);
        if (!permissionService.hasTablePermission(permissions, tableName, "SELECT")) {
            throw new SecurityException("用户无权限访问表结构: " + tableName);
        }
        
        TableSchema schema = metadataService.getTableSchema(tableName);
        
        // 过滤用户无权限的字段
        List<ColumnInfo> allowedColumns = schema.getColumns().stream()
            .filter(col -> permissionService.hasFieldPermission(permissions, tableName, col.getName()))
            .collect(Collectors.toList());
        
        return schema.toBuilder().columns(allowedColumns).build();
    }
    
    // ========== 私有辅助方法 ==========
    
    private String buildDynamicQuery(String tableName, TableFilter filter, PaginationInput pagination,
                                    List<String> fieldSelection, List<OrderByInput> orderBy,
                                    UserPermissions permissions) {
        StringBuilder sql = new StringBuilder();
        
        // SELECT子句
        sql.append("SELECT ");
        if (fieldSelection != null && !fieldSelection.isEmpty()) {
            sql.append(String.join(", ", fieldSelection));
        } else {
            sql.append("*");
        }
        
        // FROM子句
        sql.append(" FROM ").append(tableName);
        
        // WHERE子句
        List<String> whereConditions = new ArrayList<>();
        
        // 添加过滤条件
        if (filter != null && filter.getConditions() != null) {
            for (FilterCondition condition : filter.getConditions()) {
                whereConditions.add(buildFilterCondition(condition));
            }
        }
        
        // 添加行级权限过滤
        String rowLevelFilter = permissionService.getRowLevelFilter(permissions, tableName);
        if (rowLevelFilter != null) {
            whereConditions.add(rowLevelFilter);
        }
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ");
            String operator = (filter != null && filter.getOperator() != null) ? 
                filter.getOperator().name() : "AND";
            sql.append(String.join(" " + operator + " ", whereConditions));
        }
        
        // ORDER BY子句
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append(" ORDER BY ");
            List<String> orderClauses = orderBy.stream()
                .map(order -> order.getField() + " " + order.getDirection().name())
                .collect(Collectors.toList());
            sql.append(String.join(", ", orderClauses));
        }
        
        // LIMIT子句
        if (pagination != null) {
            sql.append(" LIMIT ").append(pagination.getLimit());
            if (pagination.getOffset() > 0) {
                sql.append(" OFFSET ").append(pagination.getOffset());
            }
        }
        
        return sql.toString();
    }
    
    private String buildFilterCondition(FilterCondition condition) {
        String field = condition.getField();
        ComparisonOperator operator = condition.getOperator();
        Object value = condition.getValue();
        
        switch (operator) {
            case EQ:
                return field + " = '" + value + "'";
            case NE:
                return field + " != '" + value + "'";
            case GT:
                return field + " > " + value;
            case GTE:
                return field + " >= " + value;
            case LT:
                return field + " < " + value;
            case LTE:
                return field + " <= " + value;
            case LIKE:
                return field + " LIKE '" + value + "'";
            case NOT_LIKE:
                return field + " NOT LIKE '" + value + "'";
            case IN:
                List<Object> values = condition.getValues();
                String inValues = values.stream()
                    .map(v -> "'" + v + "'")
                    .collect(Collectors.joining(", "));
                return field + " IN (" + inValues + ")";
            case NOT_IN:
                List<Object> notInValues = condition.getValues();
                String notInValuesStr = notInValues.stream()
                    .map(v -> "'" + v + "'")
                    .collect(Collectors.joining(", "));
                return field + " NOT IN (" + notInValuesStr + ")";
            case IS_NULL:
                return field + " IS NULL";
            case IS_NOT_NULL:
                return field + " IS NOT NULL";
            default:
                throw new IllegalArgumentException("不支持的操作符: " + operator);
        }
    }
    
    private String buildCacheKey(String tableName, TableFilter filter, PaginationInput pagination,
                                List<String> fieldSelection, List<OrderByInput> orderBy) {
        return String.format("query:%s:%s:%s:%s:%s", 
            tableName,
            filter != null ? filter.hashCode() : "null",
            pagination != null ? pagination.hashCode() : "null",
            fieldSelection != null ? fieldSelection.hashCode() : "null",
            orderBy != null ? orderBy.hashCode() : "null");
    }
    
    private int getTotalCount(String tableName, TableFilter filter, UserPermissions permissions) {
        // 构建COUNT查询
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName);
        
        List<String> whereConditions = new ArrayList<>();
        if (filter != null && filter.getConditions() != null) {
            for (FilterCondition condition : filter.getConditions()) {
                whereConditions.add(buildFilterCondition(condition));
            }
        }
        
        String rowLevelFilter = permissionService.getRowLevelFilter(permissions, tableName);
        if (rowLevelFilter != null) {
            whereConditions.add(rowLevelFilter);
        }
        
        if (!whereConditions.isEmpty()) {
            countSql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }
        
        return jdbcTemplate.queryForObject(countSql.toString(), Integer.class);
    }
    
    private boolean hasNextPage(PaginationInput pagination, int totalCount) {
        if (pagination == null) return false;
        return (pagination.getOffset() + pagination.getLimit()) < totalCount;
    }
    
    private QueryMetadata buildQueryMetadata(String sql, long executionTime) {
        return QueryMetadata.builder()
            .executionPlan("Trino执行计划") // 实际应该从Trino获取
            .resourceGroup("default")
            .executionTime((int) executionTime)
            .cacheHit(false)
            .build();
    }
    
    private QueryResult executeAsyncQuery(AsyncQueryInput input, String userId, String taskId) {
        // 实际的异步查询执行逻辑
        // 这里应该调用Trino的异步查询接口
        return QueryResult.builder()
            .taskId(taskId)
            .status(QueryStatus.COMPLETED)
            .data(new ArrayList<>())
            .build();
    }
    
    private String determineResourceGroup(AsyncQueryInput input, String userId) {
        // 根据用户、查询类型、优先级等确定资源组
        if (input.getPriority() == QueryPriority.HIGH || input.getPriority() == QueryPriority.URGENT) {
            return "high_priority";
        }
        return "default";
    }
    
    private int estimateQueryDuration(AsyncQueryInput input) {
        // 基于历史数据估算查询执行时间
        return 60; // 默认60秒
    }
    
    private float calculateProgress(AsyncQueryTask task, CompletableFuture<QueryResult> future) {
        if (future == null) return 0.0f;
        if (future.isDone()) return 1.0f;
        
        // 基于时间估算进度
        long elapsed = System.currentTimeMillis() - task.getSubmittedAt().toEpochSecond(java.time.ZoneOffset.UTC) * 1000;
        long estimated = task.getEstimatedDuration() * 1000L;
        return Math.min(0.95f, (float) elapsed / estimated);
    }
    
    private QueryResult applyPagination(QueryResult result, PaginationInput pagination) {
        List<Map<String, Object>> data = result.getData();
        int offset = pagination.getOffset();
        int limit = pagination.getLimit();
        
        List<Map<String, Object>> paginatedData = data.stream()
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
        
        return result.toBuilder()
            .data(paginatedData)
            .hasNextPage((offset + limit) < data.size())
            .build();
    }
}