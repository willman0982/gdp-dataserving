package com.example.graphqldgstrino.service.dataplatform;

import static com.example.graphqldgstrino.model.dataplatform.DataPlatformModels.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 元数据服务 - 管理表结构、字段信息、统计信息等
 */
@Service
public class MetadataService {
    
    // 模拟元数据存储
    private final Map<String, TableMetadata> tableMetadataCache = new HashMap<>();
    private final Map<String, TableSchema> tableSchemaCache = new HashMap<>();
    private final Map<String, List<TableInfo>> databaseTablesCache = new HashMap<>();
    
    public MetadataService() {
        initializeMockData();
    }
    
    /**
     * 获取表的元数据信息
     */
    public TableMetadata getTableMetadata(String database, String tableName) {
        String key = database + "." + tableName;
        return tableMetadataCache.get(key);
    }
    
    /**
     * 获取表的Schema信息
     */
    public TableSchema getTableSchema(String database, String tableName) {
        String key = database + "." + tableName;
        return tableSchemaCache.get(key);
    }
    
    /**
     * 获取数据库中的所有表
     */
    public List<TableInfo> listTables(String database, TableListFilter filter, PaginationInput pagination) {
        List<TableInfo> tables = databaseTablesCache.getOrDefault(database, new ArrayList<>());
        
        // 应用过滤器
        if (filter != null) {
            tables = tables.stream()
                .filter(table -> applyTableFilter(table, filter))
                .collect(Collectors.toList());
        }
        
        // 应用分页
        if (pagination != null) {
            int start = pagination.getOffset();
            int end = Math.min(start + pagination.getLimit(), tables.size());
            if (start < tables.size()) {
                return tables.subList(start, end);
            }
        }
        
        return tables;
    }
    
    /**
     * 获取所有数据库列表
     */
    public List<String> listDatabases() {
        return new ArrayList<>(databaseTablesCache.keySet());
    }
    
    /**
     * 搜索表
     */
    public List<TableInfo> searchTables(String searchTerm, String database) {
        List<TableInfo> allTables = new ArrayList<>();
        
        if (database != null) {
            allTables.addAll(databaseTablesCache.getOrDefault(database, new ArrayList<>()));
        } else {
            databaseTablesCache.values().forEach(allTables::addAll);
        }
        
        return allTables.stream()
            .filter(table -> 
                table.getTableName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (table.getDescription() != null && table.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) ||
                (table.getTags() != null && table.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(searchTerm.toLowerCase())))
            )
            .collect(Collectors.toList());
    }
    
    /**
     * 获取表的统计信息
     */
    public TableStatistics getTableStatistics(String database, String tableName) {
        TableMetadata metadata = getTableMetadata(database, tableName);
        return metadata != null ? metadata.getStatistics() : null;
    }
    
    /**
     * 刷新表的元数据（从Iceberg/Trino获取最新信息）
     */
    public void refreshTableMetadata(String database, String tableName) {
        // 实际实现中，这里会调用Trino API获取最新的表元数据
        // 现在只是模拟更新时间
        String key = database + "." + tableName;
        TableMetadata metadata = tableMetadataCache.get(key);
        if (metadata != null) {
            metadata.setLastModified(LocalDateTime.now());
        }
    }
    
    /**
     * 获取字段的详细信息
     */
    public List<ColumnInfo> getTableColumns(String database, String tableName) {
        TableSchema schema = getTableSchema(database, tableName);
        return schema != null ? schema.getColumns() : new ArrayList<>();
    }
    
    /**
     * 检查表是否存在
     */
    public boolean tableExists(String database, String tableName) {
        String key = database + "." + tableName;
        return tableMetadataCache.containsKey(key);
    }
    
    /**
     * 获取表的分区信息
     */
    public List<String> getTablePartitions(String database, String tableName) {
        TableMetadata metadata = getTableMetadata(database, tableName);
        return metadata != null ? metadata.getPartitionKeys() : new ArrayList<>();
    }
    
    private boolean applyTableFilter(TableInfo table, TableListFilter filter) {
        if (filter.getNamePattern() != null && !table.getTableName().matches(filter.getNamePattern())) {
            return false;
        }
        if (filter.getTableType() != null && !filter.getTableType().equals(table.getTableType())) {
            return false;
        }
        if (filter.getOwner() != null && !filter.getOwner().equals(table.getOwner())) {
            return false;
        }
        if (filter.getModifiedAfter() != null && table.getLastModified().isBefore(filter.getModifiedAfter())) {
            return false;
        }
        if (filter.getHasData() != null && filter.getHasData() && (table.getRowCount() == null || table.getRowCount() == 0)) {
            return false;
        }
        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            if (table.getTags() == null || Collections.disjoint(table.getTags(), filter.getTags())) {
                return false;
            }
        }
        return true;
    }
    
    private void initializeMockData() {
        // 初始化模拟的元数据
        LocalDateTime now = LocalDateTime.now();
        
        // 用户表
        TableMetadata userTableMetadata = new TableMetadata();
        userTableMetadata.setTableName("users");
        userTableMetadata.setDatabase("ecommerce");
        userTableMetadata.setTableType("ICEBERG");
        userTableMetadata.setLocation("s3://data-lake/ecommerce/users/");
        userTableMetadata.setPartitionKeys(Arrays.asList("created_date"));
        userTableMetadata.setOwner("data_team");
        userTableMetadata.setDescription("用户基础信息表");
        userTableMetadata.setLastModified(now);
        
        TableStatistics userStats = new TableStatistics();
        userStats.setRowCount(1000000L);
        userStats.setDataSize(500000000L);
        userStats.setFileCount(100);
        userStats.setPartitionCount(365);
        userStats.setLastAnalyzed(now);
        userTableMetadata.setStatistics(userStats);
        
        tableMetadataCache.put("ecommerce.users", userTableMetadata);
        
        // 用户表Schema
        List<ColumnInfo> userColumns = Arrays.asList(
            createColumn("id", "BIGINT", false, "用户ID", false),
            createColumn("username", "VARCHAR(50)", false, "用户名", false),
            createColumn("email", "VARCHAR(100)", false, "邮箱", true),
            createColumn("phone", "VARCHAR(20)", true, "手机号", true),
            createColumn("created_date", "DATE", false, "创建日期", false),
            createColumn("last_login", "TIMESTAMP", true, "最后登录时间", false)
        );
        
        TableSchema userSchema = TableSchema.builder()
            .tableName("users")
            .columns(userColumns)
            .partitionColumns(Arrays.asList(createColumn("created_date", "DATE", false, "创建日期", false)))
            .primaryKeys(Arrays.asList("id"))
            .build();
        
        tableSchemaCache.put("ecommerce.users", userSchema);
        
        // 订单表
        TableMetadata orderTableMetadata = new TableMetadata();
        orderTableMetadata.setTableName("orders");
        orderTableMetadata.setDatabase("ecommerce");
        orderTableMetadata.setTableType("ICEBERG");
        orderTableMetadata.setLocation("s3://data-lake/ecommerce/orders/");
        orderTableMetadata.setPartitionKeys(Arrays.asList("order_date"));
        orderTableMetadata.setOwner("data_team");
        orderTableMetadata.setDescription("订单信息表");
        orderTableMetadata.setLastModified(now);
        
        TableStatistics orderStats = new TableStatistics();
        orderStats.setRowCount(5000000L);
        orderStats.setDataSize(2000000000L);
        orderStats.setFileCount(500);
        orderStats.setPartitionCount(365);
        orderStats.setLastAnalyzed(now);
        orderTableMetadata.setStatistics(orderStats);
        
        tableMetadataCache.put("ecommerce.orders", orderTableMetadata);
        
        // 订单表Schema
        List<ColumnInfo> orderColumns = Arrays.asList(
            createColumn("id", "BIGINT", false, "订单ID", false),
            createColumn("user_id", "BIGINT", false, "用户ID", false),
            createColumn("product_id", "BIGINT", false, "产品ID", false),
            createColumn("quantity", "INTEGER", false, "数量", false),
            createColumn("price", "DECIMAL(10,2)", false, "价格", false),
            createColumn("order_date", "DATE", false, "订单日期", false),
            createColumn("status", "VARCHAR(20)", false, "订单状态", false)
        );
        
        TableSchema orderSchema = TableSchema.builder()
            .tableName("orders")
            .columns(orderColumns)
            .partitionColumns(Arrays.asList(createColumn("order_date", "DATE", false, "订单日期", false)))
            .primaryKeys(Arrays.asList("id"))
            .build();
        
        tableSchemaCache.put("ecommerce.orders", orderSchema);
        
        // 产品表
        TableMetadata productTableMetadata = new TableMetadata();
        productTableMetadata.setTableName("products");
        productTableMetadata.setDatabase("ecommerce");
        productTableMetadata.setTableType("ICEBERG");
        productTableMetadata.setLocation("s3://data-lake/ecommerce/products/");
        productTableMetadata.setPartitionKeys(Arrays.asList("category_id"));
        productTableMetadata.setOwner("data_team");
        productTableMetadata.setDescription("产品信息表");
        productTableMetadata.setLastModified(now);
        
        TableStatistics productStats = new TableStatistics();
        productStats.setRowCount(100000L);
        productStats.setDataSize(50000000L);
        productStats.setFileCount(50);
        productStats.setPartitionCount(20);
        productStats.setLastAnalyzed(now);
        productTableMetadata.setStatistics(productStats);
        
        tableMetadataCache.put("ecommerce.products", productTableMetadata);
        
        // 产品表Schema
        List<ColumnInfo> productColumns = Arrays.asList(
            createColumn("id", "BIGINT", false, "产品ID", false),
            createColumn("name", "VARCHAR(200)", false, "产品名称", false),
            createColumn("description", "TEXT", true, "产品描述", false),
            createColumn("price", "DECIMAL(10,2)", false, "价格", false),
            createColumn("category_id", "BIGINT", false, "分类ID", false),
            createColumn("stock_quantity", "INTEGER", false, "库存数量", false),
            createColumn("created_at", "TIMESTAMP", false, "创建时间", false)
        );
        
        TableSchema productSchema = TableSchema.builder()
            .tableName("products")
            .columns(productColumns)
            .partitionColumns(Arrays.asList(createColumn("category_id", "BIGINT", false, "分类ID", false)))
            .primaryKeys(Arrays.asList("id"))
            .build();
        
        tableSchemaCache.put("ecommerce.products", productSchema);
        
        // 初始化表信息列表
        List<TableInfo> ecommerceTables = Arrays.asList(
            createTableInfo("users", "ecommerce", "ICEBERG", "用户基础信息表", Arrays.asList("user", "pii"), "data_team", now, 1000000L, 500000000L),
            createTableInfo("orders", "ecommerce", "ICEBERG", "订单信息表", Arrays.asList("order", "transaction"), "data_team", now, 5000000L, 2000000000L),
            createTableInfo("products", "ecommerce", "ICEBERG", "产品信息表", Arrays.asList("product", "catalog"), "data_team", now, 100000L, 50000000L)
        );
        
        databaseTablesCache.put("ecommerce", ecommerceTables);
        
        // 添加更多数据库和表
        List<TableInfo> analyticsTables = Arrays.asList(
            createTableInfo("user_behavior", "analytics", "ICEBERG", "用户行为分析表", Arrays.asList("analytics", "behavior"), "analytics_team", now, 50000000L, 10000000000L),
            createTableInfo("sales_summary", "analytics", "ICEBERG", "销售汇总表", Arrays.asList("sales", "summary"), "analytics_team", now, 1000000L, 200000000L)
        );
        
        databaseTablesCache.put("analytics", analyticsTables);
    }
    
    private ColumnInfo createColumn(String name, String dataType, boolean nullable, String comment, boolean sensitive) {
        ColumnInfo column = new ColumnInfo();
        column.setName(name);
        column.setDataType(dataType);
        column.setNullable(nullable);
        column.setComment(comment);
        column.setSensitive(sensitive);
        return column;
    }
    
    private TableInfo createTableInfo(String tableName, String database, String tableType, String description, 
                                     List<String> tags, String owner, LocalDateTime lastModified, Long rowCount, Long dataSize) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(tableName);
        tableInfo.setDatabase(database);
        tableInfo.setTableType(tableType);
        tableInfo.setDescription(description);
        tableInfo.setTags(tags);
        tableInfo.setOwner(owner);
        tableInfo.setCreatedAt(lastModified.minusDays(30));
        tableInfo.setLastModified(lastModified);
        tableInfo.setRowCount(rowCount);
        tableInfo.setDataSize(dataSize);
        return tableInfo;
    }
}