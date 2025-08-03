package com.example.graphqldgstrino.model.dataplatform;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data platform model definition class
 * Contains all model classes and enums related to the data platform
 */
public class DataPlatformModels {

    // ============ Metadata related models ============

    /**
     * Table metadata
     */
    public static class TableMetadata {
        private String tableName;
        private String schemaName;
        private String catalogName;
        private String tableType;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private TableSchema schema;
        private TableStatistics statistics;
        private List<IndexInfo> indexes;
        private Map<String, Object> properties;

        public TableMetadata() {}

        public TableMetadata(String tableName, String schemaName, String catalogName) {
            this.tableName = tableName;
            this.schemaName = schemaName;
            this.catalogName = catalogName;
        }

        // Getters and Setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }

        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

        public String getCatalogName() { return catalogName; }
        public void setCatalogName(String catalogName) { this.catalogName = catalogName; }

        public String getTableType() { return tableType; }
        public void setTableType(String tableType) { this.tableType = tableType; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public TableSchema getSchema() { return schema; }
        public void setSchema(TableSchema schema) { this.schema = schema; }

        public TableStatistics getStatistics() { return statistics; }
        public void setStatistics(TableStatistics statistics) { this.statistics = statistics; }

        public List<IndexInfo> getIndexes() { return indexes; }
        public void setIndexes(List<IndexInfo> indexes) { this.indexes = indexes; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Table schema information
     */
    public static class TableSchema {
        private List<ColumnInfo> columns;
        private List<String> primaryKeys;
        private List<String> partitionKeys;
        private Map<String, Object> properties;

        public TableSchema() {}

        public TableSchema(List<ColumnInfo> columns) {
            this.columns = columns;
        }

        // Getters and Setters
        public List<ColumnInfo> getColumns() { return columns; }
        public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }

        public List<String> getPrimaryKeys() { return primaryKeys; }
        public void setPrimaryKeys(List<String> primaryKeys) { this.primaryKeys = primaryKeys; }

        public List<String> getPartitionKeys() { return partitionKeys; }
        public void setPartitionKeys(List<String> partitionKeys) { this.partitionKeys = partitionKeys; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Column information
     */
    public static class ColumnInfo {
        private String name;
        private String type;
        private String comment;
        private boolean nullable;
        private Object defaultValue;
        private Integer precision;
        private Integer scale;
        private Integer length;
        private boolean isPrimaryKey;
        private boolean isPartitionKey;
        private Map<String, Object> properties;

        public ColumnInfo() {}

        public ColumnInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public boolean isNullable() { return nullable; }
        public void setNullable(boolean nullable) { this.nullable = nullable; }

        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }

        public Integer getPrecision() { return precision; }
        public void setPrecision(Integer precision) { this.precision = precision; }

        public Integer getScale() { return scale; }
        public void setScale(Integer scale) { this.scale = scale; }

        public Integer getLength() { return length; }
        public void setLength(Integer length) { this.length = length; }

        public boolean isPrimaryKey() { return isPrimaryKey; }
        public void setPrimaryKey(boolean primaryKey) { isPrimaryKey = primaryKey; }

        public boolean isPartitionKey() { return isPartitionKey; }
        public void setPartitionKey(boolean partitionKey) { isPartitionKey = partitionKey; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Table information
     */
    public static class TableInfo {
        private String tableName;
        private String schemaName;
        private String catalogName;
        private String tableType;
        private String comment;
        private Long rowCount;
        private Long sizeInBytes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Map<String, Object> properties;

        public TableInfo() {}

        public TableInfo(String tableName, String schemaName, String catalogName) {
            this.tableName = tableName;
            this.schemaName = schemaName;
            this.catalogName = catalogName;
        }

        // Getters and Setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }

        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

        public String getCatalogName() { return catalogName; }
        public void setCatalogName(String catalogName) { this.catalogName = catalogName; }

        public String getTableType() { return tableType; }
        public void setTableType(String tableType) { this.tableType = tableType; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public Long getRowCount() { return rowCount; }
        public void setRowCount(Long rowCount) { this.rowCount = rowCount; }

        public Long getSizeInBytes() { return sizeInBytes; }
        public void setSizeInBytes(Long sizeInBytes) { this.sizeInBytes = sizeInBytes; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Table statistics information
     */
    public static class TableStatistics {
        private Long rowCount;
        private Long sizeInBytes;
        private Long columnCount;
        private Long indexCount;
        private Double avgRowSize;
        private LocalDateTime lastAnalyzed;
        private Map<String, Object> columnStatistics;
        private Map<String, Object> properties;

        public TableStatistics() {}

        public TableStatistics(Long rowCount, Long sizeInBytes) {
            this.rowCount = rowCount;
            this.sizeInBytes = sizeInBytes;
        }

        // Getters and Setters
        public Long getRowCount() { return rowCount; }
        public void setRowCount(Long rowCount) { this.rowCount = rowCount; }

        public Long getSizeInBytes() { return sizeInBytes; }
        public void setSizeInBytes(Long sizeInBytes) { this.sizeInBytes = sizeInBytes; }

        public Long getColumnCount() { return columnCount; }
        public void setColumnCount(Long columnCount) { this.columnCount = columnCount; }

        public Long getIndexCount() { return indexCount; }
        public void setIndexCount(Long indexCount) { this.indexCount = indexCount; }

        public Double getAvgRowSize() { return avgRowSize; }
        public void setAvgRowSize(Double avgRowSize) { this.avgRowSize = avgRowSize; }

        public LocalDateTime getLastAnalyzed() { return lastAnalyzed; }
        public void setLastAnalyzed(LocalDateTime lastAnalyzed) { this.lastAnalyzed = lastAnalyzed; }

        public Map<String, Object> getColumnStatistics() { return columnStatistics; }
        public void setColumnStatistics(Map<String, Object> columnStatistics) { this.columnStatistics = columnStatistics; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    // ============ Permission related models ============

    /**
     * User permissions
     */
    public static class UserPermissions {
        private String userId;
        private List<TablePermission> tablePermissions;
        private List<String> globalPermissions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Map<String, Object> properties;

        public UserPermissions() {}

        public UserPermissions(String userId) {
            this.userId = userId;
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public List<TablePermission> getTablePermissions() { return tablePermissions; }
        public void setTablePermissions(List<TablePermission> tablePermissions) { this.tablePermissions = tablePermissions; }

        public List<String> getGlobalPermissions() { return globalPermissions; }
        public void setGlobalPermissions(List<String> globalPermissions) { this.globalPermissions = globalPermissions; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Table permissions
     */
    public static class TablePermission {
        private String tableName;
        private String schemaName;
        private String catalogName;
        private boolean canRead;
        private boolean canWrite;
        private boolean canDelete;
        private boolean canUpdate;
        private List<FieldPermission> fieldPermissions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Map<String, Object> properties;

        public TablePermission() {}

        public TablePermission(String tableName, String schemaName, String catalogName) {
            this.tableName = tableName;
            this.schemaName = schemaName;
            this.catalogName = catalogName;
        }

        // Getters and Setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }

        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

        public String getCatalogName() { return catalogName; }
        public void setCatalogName(String catalogName) { this.catalogName = catalogName; }

        public boolean isCanRead() { return canRead; }
        public void setCanRead(boolean canRead) { this.canRead = canRead; }

        public boolean isCanWrite() { return canWrite; }
        public void setCanWrite(boolean canWrite) { this.canWrite = canWrite; }

        public boolean isCanDelete() { return canDelete; }
        public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }

        public boolean isCanUpdate() { return canUpdate; }
        public void setCanUpdate(boolean canUpdate) { this.canUpdate = canUpdate; }

        public List<FieldPermission> getFieldPermissions() { return fieldPermissions; }
        public void setFieldPermissions(List<FieldPermission> fieldPermissions) { this.fieldPermissions = fieldPermissions; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Field permissions
     */
    public static class FieldPermission {
        private String fieldName;
        private FieldPermissionType permissionType;
        private String maskingRule;
        private Map<String, Object> properties;

        public FieldPermission() {}

        public FieldPermission(String fieldName, FieldPermissionType permissionType) {
            this.fieldName = fieldName;
            this.permissionType = permissionType;
        }

        // Getters and Setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public FieldPermissionType getPermissionType() { return permissionType; }
        public void setPermissionType(FieldPermissionType permissionType) { this.permissionType = permissionType; }

        public String getMaskingRule() { return maskingRule; }
        public void setMaskingRule(String maskingRule) { this.maskingRule = maskingRule; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Field permission type
     */
    public enum FieldPermissionType {
        FULL_ACCESS,    // Full access
        MASKED,         // Masked access
        DENIED          // Denied access
    }

    // ============ Query related models ============

    /**
     * Table query result
     */
    public static class TableQueryResult {
        private List<Map<String, Object>> data;
        private List<ColumnInfo> columns;
        private QueryMetadata metadata;
        private Integer totalCount;
        private Integer pageSize;
        private Integer currentPage;
        private boolean hasNextPage;
        private boolean hasPreviousPage;
        private String queryId;
        private LocalDateTime executedAt;

        public TableQueryResult() {}

        public TableQueryResult(List<Map<String, Object>> data, List<ColumnInfo> columns) {
            this.data = data;
            this.columns = columns;
        }

        // Getters and Setters
        public List<Map<String, Object>> getData() { return data; }
        public void setData(List<Map<String, Object>> data) { this.data = data; }

        public List<ColumnInfo> getColumns() { return columns; }
        public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }

        public QueryMetadata getMetadata() { return metadata; }
        public void setMetadata(QueryMetadata metadata) { this.metadata = metadata; }

        public Integer getTotalCount() { return totalCount; }
        public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

        public Integer getCurrentPage() { return currentPage; }
        public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }

        public boolean isHasNextPage() { return hasNextPage; }
        public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }

        public boolean isHasPreviousPage() { return hasPreviousPage; }
        public void setHasPreviousPage(boolean hasPreviousPage) { this.hasPreviousPage = hasPreviousPage; }

        public String getQueryId() { return queryId; }
        public void setQueryId(String queryId) { this.queryId = queryId; }

        public LocalDateTime getExecutedAt() { return executedAt; }
        public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    }

    /**
     * Query metadata
     */
    public static class QueryMetadata {
        private String queryId;
        private String sql;
        private Long executionTimeMs;
        private Long rowsProcessed;
        private Long bytesProcessed;
        private String executionPlan;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String userId;
        private Map<String, Object> statistics;
        private Map<String, Object> properties;

        public QueryMetadata() {}

        public QueryMetadata(String queryId, String sql) {
            this.queryId = queryId;
            this.sql = sql;
        }

        // Getters and Setters
        public String getQueryId() { return queryId; }
        public void setQueryId(String queryId) { this.queryId = queryId; }

        public String getSql() { return sql; }
        public void setSql(String sql) { this.sql = sql; }

        public Long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

        public Long getRowsProcessed() { return rowsProcessed; }
        public void setRowsProcessed(Long rowsProcessed) { this.rowsProcessed = rowsProcessed; }

        public Long getBytesProcessed() { return bytesProcessed; }
        public void setBytesProcessed(Long bytesProcessed) { this.bytesProcessed = bytesProcessed; }

        public String getExecutionPlan() { return executionPlan; }
        public void setExecutionPlan(String executionPlan) { this.executionPlan = executionPlan; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public Map<String, Object> getStatistics() { return statistics; }
        public void setStatistics(Map<String, Object> statistics) { this.statistics = statistics; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    // ============ Async query related models ============

    /**
     * Async query task
     */
    public static class AsyncQueryTask {
        private String taskId;
        private String sql;
        private QueryStatus status;
        private String userId;
        private LocalDateTime createdAt;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String errorMessage;
        private QueryResult result;
        private Integer progress;
        private Long estimatedTimeMs;
        private Map<String, Object> properties;

        public AsyncQueryTask() {}

        public AsyncQueryTask(String taskId, String sql, String userId) {
            this.taskId = taskId;
            this.sql = sql;
            this.userId = userId;
            this.status = QueryStatus.PENDING;
            this.createdAt = LocalDateTime.now();
        }

        // Getters and Setters
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }

        public String getSql() { return sql; }
        public void setSql(String sql) { this.sql = sql; }

        public QueryStatus getStatus() { return status; }
        public void setStatus(QueryStatus status) { this.status = status; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public QueryResult getResult() { return result; }
        public void setResult(QueryResult result) { this.result = result; }

        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }

        public Long getEstimatedTimeMs() { return estimatedTimeMs; }
        public void setEstimatedTimeMs(Long estimatedTimeMs) { this.estimatedTimeMs = estimatedTimeMs; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Query status
     */
    public enum QueryStatus {
        PENDING,    // Pending
        RUNNING,    // Running
        COMPLETED,  // Completed
        FAILED,     // Failed
        CANCELLED   // Cancelled
    }

    /**
     * Async query input
     */
    public static class AsyncQueryInput {
        private String sql;
        private String userId;
        private Integer priority;
        private Integer timeoutMs;
        private String resultFormat;
        private Map<String, Object> parameters;
        private Map<String, Object> properties;

        public AsyncQueryInput() {}

        public AsyncQueryInput(String sql, String userId) {
            this.sql = sql;
            this.userId = userId;
        }

        // Getters and Setters
        public String getSql() { return sql; }
        public void setSql(String sql) { this.sql = sql; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }

        public Integer getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

        public String getResultFormat() { return resultFormat; }
        public void setResultFormat(String resultFormat) { this.resultFormat = resultFormat; }

        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Query result
     */
    public static class QueryResult {
        private List<Map<String, Object>> data;
        private List<ColumnInfo> columns;
        private Integer rowCount;
        private String resultFormat;
        private String downloadUrl;
        private Long sizeInBytes;
        private LocalDateTime generatedAt;
        private Map<String, Object> metadata;
        private Map<String, Object> properties;

        public QueryResult() {}

        public QueryResult(List<Map<String, Object>> data, List<ColumnInfo> columns) {
            this.data = data;
            this.columns = columns;
        }

        // Getters and Setters
        public List<Map<String, Object>> getData() { return data; }
        public void setData(List<Map<String, Object>> data) { this.data = data; }

        public List<ColumnInfo> getColumns() { return columns; }
        public void setColumns(List<ColumnInfo> columns) { this.columns = columns; }

        public Integer getRowCount() { return rowCount; }
        public void setRowCount(Integer rowCount) { this.rowCount = rowCount; }

        public String getResultFormat() { return resultFormat; }
        public void setResultFormat(String resultFormat) { this.resultFormat = resultFormat; }

        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

        public Long getSizeInBytes() { return sizeInBytes; }
        public void setSizeInBytes(Long sizeInBytes) { this.sizeInBytes = sizeInBytes; }

        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Query task status
     */
    public static class QueryTaskStatus {
        private String taskId;
        private QueryStatus status;
        private Integer progress;
        private String message;
        private Long estimatedTimeMs;
        private Long elapsedTimeMs;
        private LocalDateTime lastUpdated;
        private Map<String, Object> properties;

        public QueryTaskStatus() {}

        public QueryTaskStatus(String taskId, QueryStatus status) {
            this.taskId = taskId;
            this.status = status;
            this.lastUpdated = LocalDateTime.now();
        }

        // Getters and Setters
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }

        public QueryStatus getStatus() { return status; }
        public void setStatus(QueryStatus status) { this.status = status; }

        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Long getEstimatedTimeMs() { return estimatedTimeMs; }
        public void setEstimatedTimeMs(Long estimatedTimeMs) { this.estimatedTimeMs = estimatedTimeMs; }

        public Long getElapsedTimeMs() { return elapsedTimeMs; }
        public void setElapsedTimeMs(Long elapsedTimeMs) { this.elapsedTimeMs = elapsedTimeMs; }

        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    // ============ Filter and pagination related models ============

    /**
     * Table filter
     */
    public static class TableFilter {
        private List<FilterCondition> conditions;
        private String logicalOperator; // AND, OR
        private Map<String, Object> properties;

        public TableFilter() {}

        public TableFilter(List<FilterCondition> conditions) {
            this.conditions = conditions;
            this.logicalOperator = "AND"; // Default to AND
        }

        // Getters and Setters
        public List<FilterCondition> getConditions() { return conditions; }
        public void setConditions(List<FilterCondition> conditions) { this.conditions = conditions; }

        public String getLogicalOperator() { return logicalOperator; }
        public void setLogicalOperator(String logicalOperator) { this.logicalOperator = logicalOperator; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Filter condition
     */
    public static class FilterCondition {
        private String field;
        private ComparisonOperator operator;
        private Object value;
        private List<Object> values; // For IN, NOT_IN operators
        private Map<String, Object> properties;

        public FilterCondition() {}

        public FilterCondition(String field, ComparisonOperator operator, Object value) {
            this.field = field;
            this.operator = operator;
            this.value = value;
        }

        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public ComparisonOperator getOperator() { return operator; }
        public void setOperator(ComparisonOperator operator) { this.operator = operator; }

        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }

        public List<Object> getValues() { return values; }
        public void setValues(List<Object> values) { this.values = values; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Comparison operator
     */
    public enum ComparisonOperator {
        EQ,         // Equal
        NE,         // Not equal
        GT,         // Greater than
        GE,         // Greater than or equal
        LT,         // Less than
        LE,         // Less than or equal
        LIKE,       // Like
        NOT_LIKE,   // Not like
        IN,         // In
        NOT_IN,     // Not in
        IS_NULL,    // Is null
        IS_NOT_NULL // Is not null
    }

    /**
     * Order by input
     */
    public static class OrderByInput {
        private String field;
        private String direction; // ASC, DESC
        private Integer priority; // For multiple order by fields
        private Map<String, Object> properties;

        public OrderByInput() {}

        public OrderByInput(String field, String direction) {
            this.field = field;
            this.direction = direction;
        }

        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getDirection() { return direction; }
        public void setDirection(String direction) { this.direction = direction; }

        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Pagination input
     */
    public static class PaginationInput {
        private Integer page;
        private Integer size;
        private Integer offset;
        private Integer limit;
        private Map<String, Object> properties;

        public PaginationInput() {}

        public PaginationInput(Integer page, Integer size) {
            this.page = page;
            this.size = size;
        }

        // Getters and Setters
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }

        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }

        public Integer getOffset() { return offset; }
        public void setOffset(Integer offset) { this.offset = offset; }

        public Integer getLimit() { return limit; }
        public void setLimit(Integer limit) { this.limit = limit; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Table list filter
     */
    public static class TableListFilter {
        private String schemaName;
        private String catalogName;
        private String tableType;
        private String namePattern;
        private List<String> tableNames;
        private Map<String, Object> properties;

        public TableListFilter() {}

        public TableListFilter(String schemaName) {
            this.schemaName = schemaName;
        }

        // Getters and Setters
        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

        public String getCatalogName() { return catalogName; }
        public void setCatalogName(String catalogName) { this.catalogName = catalogName; }

        public String getTableType() { return tableType; }
        public void setTableType(String tableType) { this.tableType = tableType; }

        public String getNamePattern() { return namePattern; }
        public void setNamePattern(String namePattern) { this.namePattern = namePattern; }

        public List<String> getTableNames() { return tableNames; }
        public void setTableNames(List<String> tableNames) { this.tableNames = tableNames; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }

    /**
     * Index information
     */
    public static class IndexInfo {
        private String indexName;
        private String tableName;
        private String schemaName;
        private String catalogName;
        private String indexType; // BTREE, HASH, etc.
        private List<String> columns;
        private boolean isUnique;
        private boolean isPrimary;
        private String comment;
        private LocalDateTime createdAt;
        private Map<String, Object> properties;

        public IndexInfo() {}

        public IndexInfo(String indexName, String tableName, List<String> columns) {
            this.indexName = indexName;
            this.tableName = tableName;
            this.columns = columns;
        }

        // Getters and Setters
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }

        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }

        public String getSchemaName() { return schemaName; }
        public void setSchemaName(String schemaName) { this.schemaName = schemaName; }

        public String getCatalogName() { return catalogName; }
        public void setCatalogName(String catalogName) { this.catalogName = catalogName; }

        public String getIndexType() { return indexType; }
        public void setIndexType(String indexType) { this.indexType = indexType; }

        public List<String> getColumns() { return columns; }
        public void setColumns(List<String> columns) { this.columns = columns; }

        public boolean isUnique() { return isUnique; }
        public void setUnique(boolean unique) { isUnique = unique; }

        public boolean isPrimary() { return isPrimary; }
        public void setPrimary(boolean primary) { isPrimary = primary; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public Map<String, Object> getProperties() { return properties; }
        public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    }
}
