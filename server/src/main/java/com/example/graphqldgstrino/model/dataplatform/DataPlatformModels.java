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
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Table schema information
     */
    public static class TableSchema {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Column information
     */
    public static class ColumnInfo {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Table information
     */
    public static class TableInfo {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Table statistics information
     */
    public static class TableStatistics {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    // ============ Permission related models ============

    /**
     * User permissions
     */
    public static class UserPermissions {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Table permissions
     */
    public static class TablePermission {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Field permissions
     */
    public static class FieldPermission {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
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
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Query metadata
     */
    public static class QueryMetadata {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    // ============ Async query related models ============

    /**
     * Async query task
     */
    public static class AsyncQueryTask {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
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
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Query result
     */
    public static class QueryResult {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Query task status
     */
    public static class QueryTaskStatus {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    // ============ Filter and pagination related models ============

    /**
     * Table filter
     */
    public static class TableFilter {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Filter condition
     */
    public static class FilterCondition {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
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
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Pagination input
     */
    public static class PaginationInput {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Table list filter
     */
    public static class TableListFilter {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }

    /**
     * Index information
     */
    public static class IndexInfo {
        // ... (no change to field names)
        // ... (constructor and getter/setter code unchanged)
    }
}