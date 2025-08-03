# Data Platform GraphQL API

A comprehensive data middle-ground GraphQL API built with DGS Framework and Trino, providing unified access to distributed data sources with advanced features like permission control, asynchronous queries, and intelligent caching.

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client Apps   │    │   GraphQL API   │    │  Data Sources   │
│  (Any GraphQL   │◄──►│  (DGS + Spring  │◄──►│ (Trino, Hive,   │
│    Client)      │    │     Boot)       │    │  Iceberg, etc.) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   Metadata &    │
                    │   Permission    │
                    │    Services     │
                    └─────────────────┘
```

## Key Features

- **🚀 Unified Data Access** - Single GraphQL endpoint for all data sources
- **🔒 Fine-grained Permissions** - Table, field, and row-level access control
- **⚡ Asynchronous Queries** - Long-running query support with progress tracking
- **📊 Dynamic Schema Discovery** - Automatic table and column metadata detection
- **🎯 Smart Caching** - Query result caching with intelligent invalidation
- **📈 Performance Monitoring** - Detailed query execution metrics
- **🔍 Advanced Filtering** - Complex query conditions and sorting
- **📄 Pagination Support** - Efficient large dataset handling

## Prerequisites

- Java 17+
- Node.js 16+
- Maven 3.6+
- Trino server (optional - mock data available)

## Quick Start

### 1. Start the Backend (DGS Server)

```bash
# Navigate to server directory
cd server

# Build and run the Spring Boot application
mvn clean install
mvn spring-boot:run
```

The GraphQL API will be available at:
- **GraphQL Endpoint**: http://localhost:8081/graphql
- **GraphiQL Playground**: http://localhost:8081/graphiql

### 2. Start the Frontend (React + Apollo Client)

```bash
# Navigate to client directory
cd client

# Install dependencies
npm install

# Start the development server
npm start
```

The React application will be available at: http://localhost:3000

## GraphQL Schema

### Core Query Interface

```graphql
type Query {
  # Universal table query - main data access point
  queryByTable(
    tableName: String!
    filter: TableFilter
    pagination: PaginationInput
    fieldSelection: [String!]
    orderBy: [OrderByInput!]
  ): TableQueryResult!
  
  # Asynchronous query management
  submitAsyncQuery(input: AsyncQueryInput!): AsyncQueryTask!
  queryTaskStatus(taskId: ID!): QueryTaskStatus!
  getQueryResult(taskId: ID!, pagination: PaginationInput): QueryResult!
  
  # Metadata discovery
  getTableMetadata(tableName: String!): TableMetadata
  listTables(filter: TableListFilter): [TableInfo!]!
  getTableSchema(tableName: String!): TableSchema!
  
  # Permission management
  getUserPermissions(userId: ID!, tableName: String): UserPermissions!
  
  # Query history
  getQueryHistory(userId: ID!, pagination: PaginationInput): [QueryHistoryItem!]!
}
```

### Key Types

```graphql
type TableQueryResult {
  data: [JSON!]!           # Query results as JSON objects
  totalCount: Int!         # Total available records
  hasNextPage: Boolean!    # Pagination indicator
  executionTime: Int!      # Query execution time (ms)
  fromCache: Boolean!      # Cache hit indicator
  metadata: QueryMetadata! # Execution details
}

type AsyncQueryTask {
  taskId: ID!
  status: QueryStatus!
  submittedAt: DateTime!
  estimatedDuration: Int
  priority: QueryPriority!
  resourceGroup: String!
}

type TableMetadata {
  tableName: String!
  database: String!
  tableType: String!       # ICEBERG, HIVE, etc.
  location: String!
  partitionKeys: [String!]!
  statistics: TableStatistics
  owner: String!
  description: String
}
```

## Trino Setup (Optional)

If you want to use Trino instead of mock data:

1. **Install Trino**:
   ```bash
   # Download and extract Trino
   wget https://repo1.maven.org/maven2/io/trino/trino-server/435/trino-server-435.tar.gz
   tar -xzf trino-server-435.tar.gz
   cd trino-server-435
   ```

2. **Configure Trino**:
   Create `etc/config.properties`:
   ```properties
   coordinator=true
   node-scheduler.include-coordinator=true
   http-server.http.port=8080
   discovery.uri=http://localhost:8080
   ```

3. **Start Trino**:
   ```bash
   bin/launcher run
   ```

4. **Create Sample Tables**:
   ```sql
   CREATE TABLE memory.default.users (
     id VARCHAR,
     name VARCHAR,
     email VARCHAR,
     created_at VARCHAR
   );
   
   CREATE TABLE memory.default.orders (
     id VARCHAR,
     user_id VARCHAR,
     product_name VARCHAR,
     quantity INTEGER,
     price DOUBLE,
     order_date VARCHAR
   );
   ```

## API Examples

### Basic Table Query

```graphql
query {
  queryByTable(
    tableName: "sales_data"
    fieldSelection: ["customer_id", "product_name", "amount", "order_date"]
    filter: {
      conditions: [
        { field: "order_date", operator: GTE, value: "2024-01-01" }
        { field: "amount", operator: GT, value: 100 }
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
      cacheHit
    }
  }
}
```

### Asynchronous Query

```graphql
# Submit long-running query
mutation {
  submitAsyncQuery(input: {
    query: "SELECT * FROM large_dataset WHERE complex_condition = true"
    queryType: SQL
    priority: HIGH
    maxExecutionTime: 3600
    resultFormat: JSON
  }) {
    taskId
    status
    estimatedDuration
    priority
  }
}

# Check query status
query {
  queryTaskStatus(taskId: "task-123") {
    taskId
    status
    progress
    executionTime
    rowsProcessed
    resultPreview
  }
}
```

### Metadata Discovery

```graphql
# List available tables
query {
  listTables(filter: {
    database: "analytics"
    namePattern: "sales_*"
    hasData: true
  }) {
    tableName
    database
    tableType
    description
    rowCount
    dataSize
    lastModified
  }
}

# Get table schema
query {
  getTableSchema(tableName: "customer_data") {
    tableName
    columns {
      name
      dataType
      nullable
      sensitive
      comment
    }
    primaryKeys
    indexes {
      name
      columns
      unique
    }
  }
}
```

## Project Structure

```
├── server/                                       # Backend Spring Boot application
│   ├── src/main/java/com/example/graphqldgstrino/
│   │   ├── GraphqlDgsTrinoApplication.java       # Main application
│   │   ├── datafetcher/
│   │   │   └── DataPlatformDataFetcher.java      # GraphQL resolvers
│   │   ├── model/
│   │   │   ├── dataplatform/
│   │   │   │   ├── DataPlatformModels.java       # All data models
│   │   │   │   └── TableQueryResult.java         # Query result model
│   │   │   ├── Customer.java                     # Legacy models
│   │   │   ├── Order.java
│   │   │   └── Product.java
│   │   └── service/
│   │       ├── DataPlatformService.java          # Core data platform service
│   │       └── dataplatform/
│   │           ├── MetadataService.java          # Table metadata management
│   │           ├── PermissionService.java        # Access control service
│   │           └── QueryCacheService.java        # Query caching service
│   ├── src/main/resources/
│   │   ├── application.yml                       # Application configuration
│   │   └── schema/
│   │       ├── data-platform-schema.graphqls     # Data platform GraphQL schema
│   │       └── schema.graphqls                   # Legacy schema
│   ├── target/                                   # Build output
│   └── pom.xml                                   # Maven dependencies
├── client/                                       # React frontend application
│   ├── src/
│   │   ├── components/
│   │   ├── graphql/
│   │   └── App.js
│   ├── public/
│   └── package.json
├── API_REFERENCE.md                              # API documentation
└── README.md                                     # Project documentation
```

### Core Components

#### DataPlatformService
The main service orchestrating all data platform operations:
- Universal table querying with filtering and pagination
- Asynchronous query management with progress tracking
- Query result caching and optimization
- Integration with metadata and permission services

#### MetadataService
Manages table and schema metadata:
- Dynamic table discovery and schema introspection
- Table statistics and partition information
- Metadata caching for performance optimization

#### PermissionService
Handles access control and data security:
- User permission validation
- Field-level access control
- Row-level security filters
- Data masking and anonymization

#### QueryCacheService
Optimizes query performance through intelligent caching:
- Query result caching with TTL management
- Cache key generation and invalidation
- Query history tracking and analytics

## Technologies Used

### Backend
- **Spring Boot 2.7.18** - Application framework
- **DGS Framework 4.9.16** - Netflix's GraphQL framework
- **Trino JDBC 435** - Distributed SQL query engine connectivity
- **Spring JDBC** - Database operations and connection management
- **Jackson** - JSON processing and serialization
- **Spring Cache** - Query result caching
- **Java 11** - Runtime environment

### Data Platform Features
- **Dynamic Schema Discovery** - Automatic table and column detection
- **Permission-based Access Control** - Fine-grained security model
- **Asynchronous Query Processing** - Long-running query support
- **Intelligent Caching** - Multi-level caching strategy
- **Query Optimization** - Performance monitoring and optimization

### Frontend (Optional)
- **React 18.2.0** - UI framework
- **Apollo Client 3.8.7** - GraphQL client
- **GraphQL 16.8.1** - Query language

## Configuration

### Application Configuration

Update `src/main/resources/application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: data-platform-graphql-api
  datasource:
    url: jdbc:trino://localhost:8080/catalog/schema
    username: admin
    password: 
    driver-class-name: io.trino.jdbc.TrinoDriver
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=1h

dgs:
  graphql:
    graphiql:
      enabled: true
      path: /graphiql
    path: /graphql

# Data Platform Configuration
data-platform:
  query:
    default-timeout: 300  # seconds
    max-result-rows: 10000
    cache-ttl: 3600      # seconds
  permissions:
    enabled: true
    default-resource-group: "default"
  metadata:
    refresh-interval: 300 # seconds
    cache-size: 1000

logging:
  level:
    com.example.graphqldgstrino: DEBUG
    com.netflix.graphql.dgs: INFO
```

### Environment Variables

```bash
# Trino Connection
TRINO_URL=jdbc:trino://localhost:8080/catalog/schema
TRINO_USERNAME=admin
TRINO_PASSWORD=

# Security
JWT_SECRET=your-jwt-secret
ADMIN_USERS=admin,data-admin

# Performance
QUERY_CACHE_SIZE=1000
METADATA_CACHE_TTL=3600
```

## Development

### Backend Development

```bash
# Navigate to server directory
cd server

# Build and run the application
mvn clean install
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Build production JAR
mvn clean package -Pprod
```

### GraphQL Development

```bash
# Access GraphiQL playground
open http://localhost:8081/graphiql

# Test API endpoint
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ listTables { tableName database } }"}'
```

### Frontend Development (Optional)

```bash
cd client

# Install dependencies
npm install

# Start development server
npm start

# Run tests
npm test

# Build for production
npm run build
```

## Deployment

### Production Deployment

```bash
# Navigate to server directory
cd server

# Build production JAR
mvn clean package -Pprod

# Run with production profile
java -jar target/graphql-dgs-trino-demo-1.0.0.jar --spring.profiles.active=prod

# Or with Docker (from project root)
cd ..
docker build -t data-platform-api ./server
docker run -p 8081:8081 \
  -e TRINO_URL=jdbc:trino://trino-cluster:8080/catalog/schema \
  -e SPRING_PROFILES_ACTIVE=prod \
  data-platform-api
```

### Health Checks

```bash
# Application health
curl http://localhost:8081/actuator/health

# GraphQL schema introspection
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ __schema { types { name } } }"}'
```

## Usage Guidelines

### Best Practices

1. **Query Optimization**
   - Use field selection to limit returned columns
   - Apply filters early to reduce data processing
   - Use pagination for large result sets
   - Leverage caching for frequently accessed data

2. **Security**
   - Always validate user permissions before queries
   - Use row-level security for sensitive data
   - Apply field masking for PII data
   - Monitor query patterns for anomalies

3. **Performance**
   - Use asynchronous queries for long-running operations
   - Monitor query execution times and resource usage
   - Implement proper indexing on frequently queried columns
   - Use appropriate resource groups for query prioritization

### Common Patterns

```graphql
# Paginated query with filtering
query GetSalesData($offset: Int!, $limit: Int!) {
  queryByTable(
    tableName: "sales_fact"
    filter: {
      conditions: [
        { field: "sale_date", operator: GTE, value: "2024-01-01" }
      ]
    }
    pagination: { offset: $offset, limit: $limit }
    orderBy: [{ field: "sale_date", direction: DESC }]
  ) {
    data
    totalCount
    hasNextPage
    executionTime
  }
}

# Metadata exploration
query ExploreDatabase {
  listTables(filter: { database: "analytics" }) {
    tableName
    description
    rowCount
    lastModified
  }
}
```

## Troubleshooting

### Common Issues

1. **Connection Issues**
   ```bash
   # Check Trino connectivity
   curl http://trino-host:8080/v1/info
   ```

2. **Permission Errors**
   ```graphql
   # Check user permissions
   query {
     getUserPermissions(userId: "user123", tableName: "sensitive_data") {
       tablePermissions {
         permissions
         fieldPermissions {
           fieldName
           permission
         }
       }
     }
   }
   ```

3. **Performance Issues**
   - Enable query logging: `logging.level.com.example.graphqldgstrino=DEBUG`
   - Monitor cache hit rates in query metadata
   - Check Trino query plans for optimization opportunities

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes following the coding standards
4. Add tests for new functionality
5. Update documentation as needed
6. Submit a pull request

### Development Guidelines

- Follow Spring Boot and DGS best practices
- Write comprehensive tests for new features
- Update GraphQL schema documentation
- Ensure backward compatibility
- Add appropriate logging and monitoring

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For questions and support:
- Create an issue in the GitHub repository
- Check the [GraphQL Schema Documentation](src/main/resources/schema/data-platform-schema.graphqls)
- Review the [DGS Framework Documentation](https://netflix.github.io/dgs/)
- Consult the [Trino Documentation](https://trino.io/docs/)