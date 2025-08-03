package com.example.graphqldgstrino.model.dataplatform;

import java.util.List;
import java.util.Map;

/**
 * 表查询结果
 */
public class TableQueryResult {
    private List<Map<String, Object>> data;
    private Integer totalCount;
    private Boolean hasNextPage;
    private Integer executionTime;
    private Boolean fromCache;
    private DataPlatformModels.QueryMetadata metadata;

    public TableQueryResult() {}

    private TableQueryResult(Builder builder) {
        this.data = builder.data;
        this.totalCount = builder.totalCount;
        this.hasNextPage = builder.hasNextPage;
        this.executionTime = builder.executionTime;
        this.fromCache = builder.fromCache;
        this.metadata = builder.metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Map<String, Object>> data;
        private Integer totalCount;
        private Boolean hasNextPage;
        private Integer executionTime;
        private Boolean fromCache;
        private DataPlatformModels.QueryMetadata metadata;

        public Builder data(List<Map<String, Object>> data) {
            this.data = data;
            return this;
        }

        public Builder totalCount(Integer totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder hasNextPage(Boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
            return this;
        }

        public Builder executionTime(Integer executionTime) {
            this.executionTime = executionTime;
            return this;
        }

        public Builder fromCache(Boolean fromCache) {
            this.fromCache = fromCache;
            return this;
        }

        public Builder metadata(DataPlatformModels.QueryMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public TableQueryResult build() {
            return new TableQueryResult(this);
        }
    }

    // Getters
    public List<Map<String, Object>> getData() {
        return data;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public Boolean getFromCache() {
        return fromCache;
    }

    public DataPlatformModels.QueryMetadata getMetadata() {
        return metadata;
    }

    // Setters
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    public void setFromCache(Boolean fromCache) {
        this.fromCache = fromCache;
    }

    public void setMetadata(DataPlatformModels.QueryMetadata metadata) {
        this.metadata = metadata;
    }
}