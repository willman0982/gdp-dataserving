package com.example.graphqldgstrino.service.dataplatform;

import static com.example.graphqldgstrino.model.dataplatform.DataPlatformModels.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询缓存服务 - 管理查询结果缓存、查询历史等
 */
@Service
public class QueryCacheService {
    
    // 查询结果缓存
    private final Map<String, CachedQueryResult> queryResultCache = new ConcurrentHashMap<>();
    // 查询历史
    private final Map<String, List<QueryHistory>> userQueryHistory = new ConcurrentHashMap<>();
    // 缓存统计
    private final Map<String, CacheStatistics> cacheStats = new ConcurrentHashMap<>();
    
    // 缓存配置
    private static final int MAX_CACHE_SIZE = 1000;
    private static final int CACHE_TTL_MINUTES = 60;
    
    /**
     * 获取缓存的查询结果
     */
    public TableQueryResult getCachedResult(String cacheKey) {
        CachedQueryResult cached = queryResultCache.get(cacheKey);
        if (cached == null) {
            return null;
        }
        
        // 检查是否过期
        if (cached.getExpiresAt().isBefore(LocalDateTime.now())) {
            queryResultCache.remove(cacheKey);
            return null;
        }
        
        // 更新缓存统计
        updateCacheHitStats(cacheKey);
        
        return cached.getResult();
    }
    
    /**
     * 缓存查询结果
     */
    public void cacheResult(String cacheKey, TableQueryResult result, int ttlMinutes) {
        // 检查缓存大小限制
        if (queryResultCache.size() >= MAX_CACHE_SIZE) {
            evictOldestEntries();
        }
        
        CachedQueryResult cached = new CachedQueryResult();
        cached.setCacheKey(cacheKey);
        cached.setResult(result);
        cached.setCachedAt(LocalDateTime.now());
        cached.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes > 0 ? ttlMinutes : CACHE_TTL_MINUTES));
        cached.setHitCount(0);
        
        queryResultCache.put(cacheKey, cached);
        
        // 更新缓存统计
        updateCacheMissStats(cacheKey);
    }
    
    /**
     * 生成查询缓存键
     */
    public String generateCacheKey(String userId, String query, Map<String, Object> parameters) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("user:").append(userId)
                  .append(":query:").append(query.hashCode());
        
        if (parameters != null && !parameters.isEmpty()) {
            keyBuilder.append(":params:").append(parameters.hashCode());
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 检查查询是否可以被缓存
     */
    public boolean isCacheable(String query, String tableName) {
        // 简单的缓存策略：SELECT查询且不包含时间相关函数
        String upperQuery = query.toUpperCase().trim();
        
        if (!upperQuery.startsWith("SELECT")) {
            return false;
        }
        
        // 不缓存包含时间函数的查询
        String[] timeKeywords = {"NOW()", "CURRENT_TIMESTAMP", "CURRENT_DATE", "RAND()", "RANDOM()"};
        for (String keyword : timeKeywords) {
            if (upperQuery.contains(keyword)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 记录查询历史
     */
    public void recordQueryHistory(String userId, String query, String tableName, 
                                  long executionTime, int resultCount, boolean fromCache) {
        QueryHistory history = new QueryHistory();
        history.setUserId(userId);
        history.setQuery(query);
        history.setTableName(tableName);
        history.setExecutedAt(LocalDateTime.now());
        history.setExecutionTime(executionTime);
        history.setResultCount(resultCount);
        history.setFromCache(fromCache);
        
        userQueryHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(history);
        
        // 限制历史记录数量
        List<QueryHistory> userHistory = userQueryHistory.get(userId);
        if (userHistory.size() > 100) {
            userHistory.remove(0); // 移除最老的记录
        }
    }
    
    /**
     * 获取用户查询历史
     */
    public List<QueryHistory> getUserQueryHistory(String userId, int limit) {
        List<QueryHistory> history = userQueryHistory.getOrDefault(userId, new ArrayList<>());
        
        // 返回最近的查询历史
        int fromIndex = Math.max(0, history.size() - limit);
        return new ArrayList<>(history.subList(fromIndex, history.size()));
    }
    
    /**
     * 获取缓存统计信息
     */
    public CacheStatistics getCacheStatistics() {
        CacheStatistics stats = new CacheStatistics();
        stats.setTotalEntries(queryResultCache.size());
        stats.setMaxSize(MAX_CACHE_SIZE);
        stats.setDefaultTtlMinutes(CACHE_TTL_MINUTES);
        
        long totalHits = 0;
        long totalMisses = 0;
        
        for (CacheStatistics userStats : cacheStats.values()) {
            totalHits += userStats.getHitCount();
            totalMisses += userStats.getMissCount();
        }
        
        stats.setHitCount(totalHits);
        stats.setMissCount(totalMisses);
        stats.setHitRate(totalHits + totalMisses > 0 ? 
                        (double) totalHits / (totalHits + totalMisses) : 0.0);
        
        return stats;
    }
    
    /**
     * 清理过期缓存
     */
    public void cleanupExpiredCache() {
        LocalDateTime now = LocalDateTime.now();
        queryResultCache.entrySet().removeIf(entry -> 
            entry.getValue().getExpiresAt().isBefore(now));
    }
    
    /**
     * 清空指定用户的缓存
     */
    public void clearUserCache(String userId) {
        queryResultCache.entrySet().removeIf(entry -> 
            entry.getKey().startsWith("user:" + userId + ":"));
    }
    
    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        queryResultCache.clear();
        cacheStats.clear();
    }
    
    /**
     * 获取热门查询
     */
    public List<PopularQuery> getPopularQueries(int limit) {
        Map<String, PopularQuery> queryFrequency = new HashMap<>();
        
        // 统计查询频率
        for (List<QueryHistory> userHistory : userQueryHistory.values()) {
            for (QueryHistory history : userHistory) {
                String queryKey = history.getQuery();
                PopularQuery popular = queryFrequency.computeIfAbsent(queryKey, k -> {
                    PopularQuery pq = new PopularQuery();
                    pq.setQuery(k);
                    pq.setExecutionCount(0);
                    pq.setTotalExecutionTime(0L);
                    pq.setLastExecuted(history.getExecutedAt());
                    return pq;
                });
                
                popular.setExecutionCount(popular.getExecutionCount() + 1);
                popular.setTotalExecutionTime(popular.getTotalExecutionTime() + history.getExecutionTime());
                if (history.getExecutedAt().isAfter(popular.getLastExecuted())) {
                    popular.setLastExecuted(history.getExecutedAt());
                }
            }
        }
        
        // 按执行次数排序并返回前N个
        return queryFrequency.values().stream()
                .sorted((a, b) -> Integer.compare(b.getExecutionCount(), a.getExecutionCount()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private void updateCacheHitStats(String cacheKey) {
        String userId = extractUserIdFromCacheKey(cacheKey);
        CacheStatistics stats = cacheStats.computeIfAbsent(userId, k -> new CacheStatistics());
        stats.setHitCount(stats.getHitCount() + 1);
        
        // 更新缓存项的命中次数
        CachedQueryResult cached = queryResultCache.get(cacheKey);
        if (cached != null) {
            cached.setHitCount(cached.getHitCount() + 1);
        }
    }
    
    private void updateCacheMissStats(String cacheKey) {
        String userId = extractUserIdFromCacheKey(cacheKey);
        CacheStatistics stats = cacheStats.computeIfAbsent(userId, k -> new CacheStatistics());
        stats.setMissCount(stats.getMissCount() + 1);
    }
    
    private String extractUserIdFromCacheKey(String cacheKey) {
        // 从 "user:userId:query:..." 格式中提取userId
        String[] parts = cacheKey.split(":");
        return parts.length > 1 ? parts[1] : "unknown";
    }
    
    private void evictOldestEntries() {
        // 移除最老的缓存项（简单的LRU策略）
        LocalDateTime oldest = LocalDateTime.now();
        String oldestKey = null;
        
        for (Map.Entry<String, CachedQueryResult> entry : queryResultCache.entrySet()) {
            if (entry.getValue().getCachedAt().isBefore(oldest)) {
                oldest = entry.getValue().getCachedAt();
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            queryResultCache.remove(oldestKey);
        }
    }
    
    // 内部类定义
    private static class CachedQueryResult {
        private String cacheKey;
        private TableQueryResult result;
        private LocalDateTime cachedAt;
        private LocalDateTime expiresAt;
        private int hitCount;
        
        // Getters and Setters
        public String getCacheKey() { return cacheKey; }
        public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
        public TableQueryResult getResult() { return result; }
        public void setResult(TableQueryResult result) { this.result = result; }
        public LocalDateTime getCachedAt() { return cachedAt; }
        public void setCachedAt(LocalDateTime cachedAt) { this.cachedAt = cachedAt; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public int getHitCount() { return hitCount; }
        public void setHitCount(int hitCount) { this.hitCount = hitCount; }
    }
    
    private static class QueryHistory {
        private String userId;
        private String query;
        private String tableName;
        private LocalDateTime executedAt;
        private long executionTime;
        private int resultCount;
        private boolean fromCache;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        public LocalDateTime getExecutedAt() { return executedAt; }
        public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        public int getResultCount() { return resultCount; }
        public void setResultCount(int resultCount) { this.resultCount = resultCount; }
        public boolean isFromCache() { return fromCache; }
        public void setFromCache(boolean fromCache) { this.fromCache = fromCache; }
    }
    
    private static class CacheStatistics {
        private int totalEntries;
        private int maxSize;
        private int defaultTtlMinutes;
        private long hitCount;
        private long missCount;
        private double hitRate;
        
        // Getters and Setters
        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
        public int getDefaultTtlMinutes() { return defaultTtlMinutes; }
        public void setDefaultTtlMinutes(int defaultTtlMinutes) { this.defaultTtlMinutes = defaultTtlMinutes; }
        public long getHitCount() { return hitCount; }
        public void setHitCount(long hitCount) { this.hitCount = hitCount; }
        public long getMissCount() { return missCount; }
        public void setMissCount(long missCount) { this.missCount = missCount; }
        public double getHitRate() { return hitRate; }
        public void setHitRate(double hitRate) { this.hitRate = hitRate; }
    }
    
    private static class PopularQuery {
        private String query;
        private int executionCount;
        private long totalExecutionTime;
        private LocalDateTime lastExecuted;
        
        // Getters and Setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public int getExecutionCount() { return executionCount; }
        public void setExecutionCount(int executionCount) { this.executionCount = executionCount; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public void setTotalExecutionTime(long totalExecutionTime) { this.totalExecutionTime = totalExecutionTime; }
        public LocalDateTime getLastExecuted() { return lastExecuted; }
        public void setLastExecuted(LocalDateTime lastExecuted) { this.lastExecuted = lastExecuted; }
    }
}