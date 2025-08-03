package com.example.graphqldgstrino.service.dataplatform;

import static com.example.graphqldgstrino.model.dataplatform.DataPlatformModels.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务 - 管理用户权限、表级权限、字段级权限等
 */
@Service
public class PermissionService {
    
    // 模拟权限存储
    private final Map<String, UserPermissions> userPermissionsCache = new HashMap<>();
    private final Map<String, Set<String>> rolePermissionsCache = new HashMap<>();
    private final Map<String, List<String>> userRolesCache = new HashMap<>();
    
    public PermissionService() {
        initializeMockPermissions();
    }
    
    /**
     * 获取用户权限
     */
    public UserPermissions getUserPermissions(String userId) {
        return userPermissionsCache.get(userId);
    }
    
    /**
     * 检查用户是否有表的访问权限
     */
    public boolean hasTablePermission(String userId, String database, String tableName, String permission) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return false;
        }
        
        // 检查全局权限
        if (userPerms.getGlobalPermissions() != null && userPerms.getGlobalPermissions().contains("ADMIN")) {
            return true;
        }
        
        // 检查表级权限
        if (userPerms.getTablePermissions() != null) {
            for (TablePermission tablePermission : userPerms.getTablePermissions()) {
                if (matchesTable(tablePermission, database, tableName)) {
                    return tablePermission.getPermissions() != null && 
                           tablePermission.getPermissions().contains(permission);
                }
            }
        }
        
        return false;
    }
    
    /**
     * 检查用户是否有字段的访问权限
     */
    public boolean hasFieldPermission(String userId, String database, String tableName, String fieldName) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return false;
        }
        
        // 检查全局权限
        if (userPerms.getGlobalPermissions() != null && userPerms.getGlobalPermissions().contains("ADMIN")) {
            return true;
        }
        
        // 检查表级权限
        if (userPerms.getTablePermissions() != null) {
            for (TablePermission tablePermission : userPerms.getTablePermissions()) {
                if (matchesTable(tablePermission, database, tableName)) {
                    // 如果没有字段级权限配置，默认允许
                    if (tablePermission.getFieldPermissions() == null || tablePermission.getFieldPermissions().isEmpty()) {
                        return tablePermission.getPermissions() != null && 
                               tablePermission.getPermissions().contains("READ");
                    }
                    
                    // 检查字段级权限
                    for (FieldPermission fieldPermission : tablePermission.getFieldPermissions()) {
                        if (fieldPermission.getFieldName().equals(fieldName)) {
                            return fieldPermission.getPermission() == FieldPermissionType.ALLOW;
                        }
                    }
                    
                    // 如果字段没有明确配置，默认拒绝
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取用户可访问的字段列表
     */
    public List<String> getAccessibleFields(String userId, String database, String tableName, List<String> allFields) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return new ArrayList<>();
        }
        
        // 检查全局权限
        if (userPerms.getGlobalPermissions() != null && userPerms.getGlobalPermissions().contains("ADMIN")) {
            return new ArrayList<>(allFields);
        }
        
        // 检查表级权限
        if (userPerms.getTablePermissions() != null) {
            for (TablePermission tablePermission : userPerms.getTablePermissions()) {
                if (matchesTable(tablePermission, database, tableName)) {
                    if (tablePermission.getFieldPermissions() == null || tablePermission.getFieldPermissions().isEmpty()) {
                        // 如果没有字段级权限配置，返回所有字段
                        return new ArrayList<>(allFields);
                    }
                    
                    // 根据字段权限过滤
                    return allFields.stream()
                        .filter(field -> {
                            for (FieldPermission fieldPermission : tablePermission.getFieldPermissions()) {
                                if (fieldPermission.getFieldName().equals(field)) {
                                    return fieldPermission.getPermission() == FieldPermissionType.ALLOW;
                                }
                            }
                            return false; // 默认拒绝未配置的字段
                        })
                        .collect(Collectors.toList());
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 获取字段的脱敏规则
     */
    public String getFieldMaskingRule(String userId, String database, String tableName, String fieldName) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return null;
        }
        
        // 管理员不需要脱敏
        if (userPerms.getGlobalPermissions() != null && userPerms.getGlobalPermissions().contains("ADMIN")) {
            return null;
        }
        
        if (userPerms.getTablePermissions() != null) {
            for (TablePermission tablePermission : userPerms.getTablePermissions()) {
                if (matchesTable(tablePermission, database, tableName)) {
                    if (tablePermission.getFieldPermissions() != null) {
                        for (FieldPermission fieldPermission : tablePermission.getFieldPermissions()) {
                            if (fieldPermission.getFieldName().equals(fieldName)) {
                                if (fieldPermission.getPermission() == FieldPermissionType.MASK) {
                                    return fieldPermission.getMaskingRule();
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * 获取用户可使用的资源组
     */
    public List<String> getUserResourceGroups(String userId) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return Arrays.asList("default"); // 默认资源组
        }
        
        List<String> resourceGroups = userPerms.getResourceGroups();
        return resourceGroups != null && !resourceGroups.isEmpty() ? resourceGroups : Arrays.asList("default");
    }
    
    /**
     * 获取用户的查询超时限制
     */
    public Integer getUserMaxQueryTimeout(String userId) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return 300; // 默认5分钟
        }
        
        return userPerms.getMaxQueryTimeout() != null ? userPerms.getMaxQueryTimeout() : 300;
    }
    
    /**
     * 获取用户的最大结果行数限制
     */
    public Integer getUserMaxResultRows(String userId) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return 10000; // 默认1万行
        }
        
        return userPerms.getMaxResultRows() != null ? userPerms.getMaxResultRows() : 10000;
    }
    
    /**
     * 获取表的行级过滤条件
     */
    public String getRowLevelFilter(String userId, String database, String tableName) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return null;
        }
        
        // 管理员不需要行级过滤
        if (userPerms.getGlobalPermissions() != null && userPerms.getGlobalPermissions().contains("ADMIN")) {
            return null;
        }
        
        if (userPerms.getTablePermissions() != null) {
            for (TablePermission tablePermission : userPerms.getTablePermissions()) {
                if (matchesTable(tablePermission, database, tableName)) {
                    return tablePermission.getRowLevelFilter();
                }
            }
        }
        
        return null;
    }
    
    /**
     * 检查用户是否可以访问指定数据库
     */
    public boolean canAccessDatabase(String userId, String database) {
        UserPermissions userPerms = getUserPermissions(userId);
        if (userPerms == null) {
            return false;
        }
        
        // 检查全局权限
        if (userPerms.getGlobalPermissions() != null && userPerms.getGlobalPermissions().contains("ADMIN")) {
            return true;
        }
        
        // 检查是否有该数据库下任何表的权限
        if (userPerms.getTablePermissions() != null) {
            return userPerms.getTablePermissions().stream()
                .anyMatch(tp -> database.equals(tp.getDatabase()));
        }
        
        return false;
    }
    
    private boolean matchesTable(TablePermission tablePermission, String database, String tableName) {
        return database.equals(tablePermission.getDatabase()) && 
               tableName.equals(tablePermission.getTableName());
    }
    
    private void initializeMockPermissions() {
        // 初始化管理员用户
        UserPermissions adminPerms = new UserPermissions();
        adminPerms.setUserId("admin");
        adminPerms.setGlobalPermissions(Arrays.asList("ADMIN", "READ", "WRITE", "DELETE"));
        adminPerms.setResourceGroups(Arrays.asList("admin", "high_priority", "default"));
        adminPerms.setMaxQueryTimeout(3600); // 1小时
        adminPerms.setMaxResultRows(1000000); // 100万行
        userPermissionsCache.put("admin", adminPerms);
        
        // 初始化普通用户
        UserPermissions userPerms = new UserPermissions();
        userPerms.setUserId("user1");
        userPerms.setGlobalPermissions(Arrays.asList("READ"));
        userPerms.setResourceGroups(Arrays.asList("default"));
        userPerms.setMaxQueryTimeout(600); // 10分钟
        userPerms.setMaxResultRows(50000); // 5万行
        
        // 设置表级权限
        List<TablePermission> tablePermissions = new ArrayList<>();
        
        // 用户表权限（有字段级限制）
        TablePermission userTablePerm = new TablePermission();
        userTablePerm.setDatabase("ecommerce");
        userTablePerm.setTableName("users");
        userTablePerm.setPermissions(Arrays.asList("READ"));
        userTablePerm.setMaxRows(10000);
        
        // 字段级权限
        List<FieldPermission> userFieldPerms = Arrays.asList(
            createFieldPermission("id", FieldPermissionType.ALLOW, null),
            createFieldPermission("username", FieldPermissionType.ALLOW, null),
            createFieldPermission("email", FieldPermissionType.MASK, "email_mask"),
            createFieldPermission("phone", FieldPermissionType.MASK, "phone_mask"),
            createFieldPermission("created_date", FieldPermissionType.ALLOW, null),
            createFieldPermission("last_login", FieldPermissionType.ALLOW, null)
        );
        userTablePerm.setFieldPermissions(userFieldPerms);
        tablePermissions.add(userTablePerm);
        
        // 订单表权限
        TablePermission orderTablePerm = new TablePermission();
        orderTablePerm.setDatabase("ecommerce");
        orderTablePerm.setTableName("orders");
        orderTablePerm.setPermissions(Arrays.asList("READ"));
        orderTablePerm.setRowLevelFilter("user_id = '" + userPerms.getUserId() + "'"); // 只能看自己的订单
        orderTablePerm.setMaxRows(5000);
        tablePermissions.add(orderTablePerm);
        
        // 产品表权限（完全开放）
        TablePermission productTablePerm = new TablePermission();
        productTablePerm.setDatabase("ecommerce");
        productTablePerm.setTableName("products");
        productTablePerm.setPermissions(Arrays.asList("READ"));
        productTablePerm.setMaxRows(1000);
        tablePermissions.add(productTablePerm);
        
        userPerms.setTablePermissions(tablePermissions);
        userPermissionsCache.put("user1", userPerms);
        
        // 初始化分析师用户
        UserPermissions analystPerms = new UserPermissions();
        analystPerms.setUserId("analyst1");
        analystPerms.setGlobalPermissions(Arrays.asList("READ"));
        analystPerms.setResourceGroups(Arrays.asList("analytics", "default"));
        analystPerms.setMaxQueryTimeout(1800); // 30分钟
        analystPerms.setMaxResultRows(500000); // 50万行
        
        List<TablePermission> analystTablePermissions = new ArrayList<>();
        
        // 可以访问所有ecommerce表，但用户表有字段限制
        TablePermission analystUserTablePerm = new TablePermission();
        analystUserTablePerm.setDatabase("ecommerce");
        analystUserTablePerm.setTableName("users");
        analystUserTablePerm.setPermissions(Arrays.asList("READ"));
        
        List<FieldPermission> analystUserFieldPerms = Arrays.asList(
            createFieldPermission("id", FieldPermissionType.ALLOW, null),
            createFieldPermission("username", FieldPermissionType.HASH, null),
            createFieldPermission("email", FieldPermissionType.DENY, null),
            createFieldPermission("phone", FieldPermissionType.DENY, null),
            createFieldPermission("created_date", FieldPermissionType.ALLOW, null),
            createFieldPermission("last_login", FieldPermissionType.ALLOW, null)
        );
        analystUserTablePerm.setFieldPermissions(analystUserFieldPerms);
        analystTablePermissions.add(analystUserTablePerm);
        
        // 完全访问订单和产品表
        TablePermission analystOrderTablePerm = new TablePermission();
        analystOrderTablePerm.setDatabase("ecommerce");
        analystOrderTablePerm.setTableName("orders");
        analystOrderTablePerm.setPermissions(Arrays.asList("READ"));
        analystTablePermissions.add(analystOrderTablePerm);
        
        TablePermission analystProductTablePerm = new TablePermission();
        analystProductTablePerm.setDatabase("ecommerce");
        analystProductTablePerm.setTableName("products");
        analystProductTablePerm.setPermissions(Arrays.asList("READ"));
        analystTablePermissions.add(analystProductTablePerm);
        
        // 访问analytics数据库
        TablePermission analystAnalyticsTablePerm = new TablePermission();
        analystAnalyticsTablePerm.setDatabase("analytics");
        analystAnalyticsTablePerm.setTableName("user_behavior");
        analystAnalyticsTablePerm.setPermissions(Arrays.asList("READ"));
        analystTablePermissions.add(analystAnalyticsTablePerm);
        
        TablePermission analystSalesTablePerm = new TablePermission();
        analystSalesTablePerm.setDatabase("analytics");
        analystSalesTablePerm.setTableName("sales_summary");
        analystSalesTablePerm.setPermissions(Arrays.asList("READ"));
        analystTablePermissions.add(analystSalesTablePerm);
        
        analystPerms.setTablePermissions(analystTablePermissions);
        userPermissionsCache.put("analyst1", analystPerms);
    }
    
    private FieldPermission createFieldPermission(String fieldName, FieldPermissionType permission, String maskingRule) {
        FieldPermission fieldPerm = new FieldPermission();
        fieldPerm.setFieldName(fieldName);
        fieldPerm.setPermission(permission);
        fieldPerm.setMaskingRule(maskingRule);
        return fieldPerm;
    }
}