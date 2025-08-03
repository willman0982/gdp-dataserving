# Data Platform GraphQL API Reference

This document provides detailed API reference for the Data Platform GraphQL API.

## Table of Contents

- [Authentication](#authentication)
- [Core Queries](#core-queries)
- [Mutations](#mutations)
- [Types Reference](#types-reference)
- [Input Types](#input-types)
- [Enums](#enums)
- [Error Handling](#error-handling)

## Authentication

The API supports JWT-based authentication. Include the JWT token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

## Core Queries

### queryByTable

Universal table query interface for accessing any table in the data platform.

**Signature:**
```graphql
queryByTable(
  tableName: String!
  filter: TableFilter
  pagination: PaginationInput
  fieldSelection: [String!]
  orderBy: [OrderByInput!]
): TableQueryResult!
```

**Parameters:**
- `tableName`: Target table name (required)
- `filter`: Query conditions and filters
- `pagination`: Limit and offset for result pagination
- `fieldSelection`: Specific columns to return
- `orderBy`: Sorting specifications

**Example:**
```graphql
query {
  queryByTable(
    tableName: "customer_orders"
    fieldSelection: ["customer_id", "order_date", "total_amount"]
    filter: {
      conditions: [
        { field: "order_date", operator: GTE, value: "2024-01-01" }
        { field: "total_amount", operator: GT, value: 100 }
      ]
      operator: AND
    }
    orderBy: [{ field: "order_date", direction: DESC }]
    pagination: { offset: 0, limit: 50 }
  ) {
    data
    totalCount
    hasNextPage
    executionTime
    fromCache
    metadata {
      resourceGroup
      bytesProcessed
      rowsProcessed
    }
  }
}
```

### submitAsyncQuery

Submit long-running queries for asynchronous execution.

**Signature:**
```graphql
submitAsyncQuery(input: AsyncQueryInput!): AsyncQueryTask!
```

**Example:**
```graphql
mutation {
  submitAsyncQuery(input: {
    query: "SELECT * FROM large_dataset WHERE complex_analysis = true"
    queryType: SQL
    priority: HIGH
    maxExecutionTime: 3600
    resultFormat: JSON
    cacheEnabled: true
  }) {
    taskId
    status
    estimatedDuration
    priority
    resourceGroup
  }
}
```

### queryTaskStatus

Check the status of an asynchronous query.

**Signature:**
```graphql
queryTaskStatus(taskId: ID!): QueryTaskStatus!
```

**Example:**
```graphql
query {
  queryTaskStatus(taskId: "async-task-123") {
    taskId
    status
    progress
    submittedAt
    startedAt
    completedAt
    executionTime
    rowsProcessed
    error
    resultPreview
  }
}
```

### getQueryResult

Retrieve results from a completed asynchronous query.

**Signature:**
```graphql
getQueryResult(taskId: ID!, pagination: PaginationInput): QueryResult!
```

### listTables

Discover available tables in the data platform.

**Signature:**
```graphql
listTables(filter: TableListFilter): [TableInfo!]!
```

**Example:**
```graphql
query {
  listTables(filter: {
    database: "analytics"
    namePattern: "customer_*"
    hasData: true
    modifiedAfter: "2024-01-01T00:00:00Z"
  }) {
    tableName
    database
    tableType
    description
    tags
    owner
    rowCount
    dataSize
    lastModified
  }
}
```

### getTableSchema

Get detailed schema information for a specific table.

**Signature:**
```graphql
getTableSchema(tableName: String!): TableSchema!
```

**Example:**
```graphql
query {
  getTableSchema(tableName: "customer_data") {
    tableName
    columns {
      name
      dataType
      nullable
      comment
      sensitive
      tags
    }
    partitionColumns {
      name
      dataType
    }
    primaryKeys
    indexes {
      name
      columns
      unique
      type
    }
  }
}
```

### getUserPermissions

Retrieve user permissions for data access.

**Signature:**
```graphql
getUserPermissions(userId: ID!, tableName: String): UserPermissions!
```

## Mutations

### cancelAsyncQuery

Cancel a running asynchronous query.

**Signature:**
```graphql
cancelAsyncQuery(taskId: ID!): Boolean!
```

### clearQueryCache

Clear cached query results matching a pattern.

**Signature:**
```graphql
clearQueryCache(pattern: String): Boolean!
```

### updateUserPermissions

Update user permissions (admin only).

**Signature:**
```graphql
updateUserPermissions(input: UpdatePermissionsInput!): UserPermissions!
```

## Types Reference

### TableQueryResult

Result of a table query operation.

```graphql
type TableQueryResult {
  data: [JSON!]!           # Query results as JSON objects
  totalCount: Int!         # Total available records
  hasNextPage: Boolean!    # Pagination indicator
  executionTime: Int!      # Query execution time (ms)
  fromCache: Boolean!      # Cache hit indicator
  metadata: QueryMetadata! # Execution details
}
```

### AsyncQueryTask

Represents an asynchronous query task.

```graphql
type AsyncQueryTask {
  taskId: ID!
  status: QueryStatus!
  submittedAt: DateTime!
  estimatedDuration: Int   # Estimated execution time (seconds)
  priority: QueryPriority!
  resourceGroup: String!
}
```

### TableMetadata

Metadata information about a table.

```graphql
type TableMetadata {
  tableName: String!
  database: String!
  tableType: String!       # ICEBERG, HIVE, DELTA, etc.
  location: String!
  partitionKeys: [String!]!
  sortKeys: [String!]!
  properties: JSON!
  statistics: TableStatistics
  lastModified: DateTime!
  owner: String!
  description: String
}
```

### UserPermissions

User access permissions.

```graphql
type UserPermissions {
  userId: ID!
  tablePermissions: [TablePermission!]!
  globalPermissions: [String!]!
  resourceGroups: [String!]!
  maxQueryTimeout: Int!    # Maximum query timeout (seconds)
  maxResultRows: Int!      # Maximum result rows
}
```

## Input Types

### TableFilter

Filtering conditions for table queries.

```graphql
input TableFilter {
  conditions: [FilterCondition!]!
  operator: LogicalOperator = AND
}
```

### FilterCondition

Individual filter condition.

```graphql
input FilterCondition {
  field: String!
  operator: ComparisonOperator!
  value: JSON!
  values: [JSON!]          # For IN, NOT_IN operators
}
```

### PaginationInput

Pagination parameters.

```graphql
input PaginationInput {
  offset: Int = 0
  limit: Int = 100
  cursor: String           # For cursor-based pagination
}
```

### OrderByInput

Sorting specification.

```graphql
input OrderByInput {
  field: String!
  direction: SortDirection = ASC
}
```

## Enums

### QueryStatus

Status of query execution.

```graphql
enum QueryStatus {
  SUBMITTED   # Query submitted to queue
  QUEUED      # Waiting in execution queue
  RUNNING     # Currently executing
  COMPLETED   # Successfully completed
  FAILED      # Execution failed
  CANCELLED   # Cancelled by user
  TIMEOUT     # Execution timed out
}
```

### ComparisonOperator

Comparison operators for filtering.

```graphql
enum ComparisonOperator {
  EQ          # Equal
  NE          # Not equal
  GT          # Greater than
  GTE         # Greater than or equal
  LT          # Less than
  LTE         # Less than or equal
  LIKE        # Pattern matching
  NOT_LIKE    # Negative pattern matching
  IN          # In list
  NOT_IN      # Not in list
  IS_NULL     # Is null
  IS_NOT_NULL # Is not null
  BETWEEN     # Between two values
}
```

### FieldPermissionType

Field-level permission types.

```graphql
enum FieldPermissionType {
  ALLOW       # Full access
  DENY        # No access
  MASK        # Masked/redacted access
  HASH        # Hashed access
}
```

## Error Handling

### Common Error Types

1. **ValidationError**: Invalid input parameters
2. **PermissionError**: Insufficient permissions
3. **ResourceNotFoundError**: Table or resource not found
4. **QueryExecutionError**: Query execution failed
5. **TimeoutError**: Query execution timeout

### Error Response Format

```json
{
  "errors": [
    {
      "message": "Table 'unknown_table' not found",
      "locations": [{ "line": 2, "column": 3 }],
      "path": ["queryByTable"],
      "extensions": {
        "code": "RESOURCE_NOT_FOUND",
        "tableName": "unknown_table"
      }
    }
  ],
  "data": null
}
```

### Error Codes

- `VALIDATION_ERROR`: Input validation failed
- `PERMISSION_DENIED`: Insufficient permissions
- `RESOURCE_NOT_FOUND`: Requested resource not found
- `QUERY_EXECUTION_ERROR`: Query execution failed
- `TIMEOUT_ERROR`: Operation timed out
- `RATE_LIMIT_EXCEEDED`: Rate limit exceeded
- `INTERNAL_ERROR`: Internal server error

## Rate Limiting

The API implements rate limiting based on:
- User identity
- Query complexity
- Resource usage
- Time windows

Rate limit headers are included in responses:
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1640995200
```

## Monitoring and Metrics

Query execution metrics are available in the `QueryMetadata` type:

```graphql
type QueryMetadata {
  executionPlan: String    # Query execution plan
  resourceGroup: String!   # Resource group used
  trinoQueryId: String     # Trino query identifier
  bytesProcessed: Long     # Data processed (bytes)
  rowsProcessed: Long      # Rows processed
  cpuTime: Int            # CPU time (milliseconds)
  wallTime: Int           # Wall clock time (milliseconds)
  peakMemory: Long        # Peak memory usage (bytes)
  spilledBytes: Long      # Spilled data (bytes)
  cacheHit: Boolean!      # Cache hit indicator
  partitionsPruned: Int   # Partitions pruned
  partitionsTotal: Int    # Total partitions
}
```