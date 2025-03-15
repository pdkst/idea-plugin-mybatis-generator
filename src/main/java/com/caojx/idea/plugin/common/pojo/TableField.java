package com.caojx.idea.plugin.common.pojo;

import com.caojx.idea.plugin.common.utils.JdbcTypeMappingHandler;
import com.google.common.base.CaseFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.JDBCType;
import java.util.Map;

/**
 * 表属性模型
 *
 * @author caojx
 * @date 2022/4/10 4:00 PM
 */
@Data
public class TableField implements Serializable {

    /**
     * 字段列名
     */
    private String columnName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 属性名
     */
    private String name;

    /**
     * 类型
     */
    private Class<?> type;

    /**
     * jdbcType
     */
    private String jdbcTypeName;

    /**
     * 是否主键
     */
    private boolean primaryKeyFlag;

    /**
     * 是否为JDBCDateColumn
     */
    private boolean jdbcDateFlag;

    /**
     * 是否为JDBCTimeColumn
     */
    private boolean jdbcTimeFlag;

    /**
     * 是否blob类型
     */
    private boolean blobFlag;

    /**
     * 自定义jdbc映射类型
     */
    private Map<JDBCType, Class<?>> customerJdbcTypeMappingMap;

    /**
     * 构造器
     */
    public TableField() {
    }

    /**
     * 构造器
     *
     * @param columnName     字段列名
     * @param comment        注释
     * @param sqlType        类型
     * @param primaryKeyFlag 是否主键
     */
    public TableField(String columnName, String comment, int sqlType, boolean primaryKeyFlag, Map<JDBCType, Class<?>> customerJdbcTypeMappingMap) {
        this.columnName = columnName;
        this.comment = comment;
        this.name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);

        JdbcTypeMappingHandler jdbcTypeMappingHandler = new JdbcTypeMappingHandler(customerJdbcTypeMappingMap);
        this.type = jdbcTypeMappingHandler.convertJavaType(sqlType);
        this.jdbcTypeName = jdbcTypeMappingHandler.convertJdbcType(sqlType);
        this.primaryKeyFlag = primaryKeyFlag;
        this.jdbcDateFlag = jdbcTypeMappingHandler.isJDBCDateColumn(sqlType);
        this.jdbcTimeFlag = jdbcTypeMappingHandler.isJDBCTimeColumn(sqlType);
        this.blobFlag = jdbcTypeMappingHandler.isBLOBColumn(sqlType);
    }

    public String getTypeSimpleName() {
        return type.getSimpleName();
    }

    public String getFullClassName() {
        return type.getName();
    }

    public boolean isImport() {
        String fullClassName = getFullClassName();
        return !type.isPrimitive() && !"java.lang".equals(StringUtils.substringBeforeLast(fullClassName, ".")) && !"byte[]".equals(type.getSimpleName());
    }
}
