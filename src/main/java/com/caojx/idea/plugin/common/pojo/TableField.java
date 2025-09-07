package com.caojx.idea.plugin.common.pojo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 表属性模型
 *
 * @author caojx
 * @since 2022/4/10 4:00 PM
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

    public String getTypeSimpleName() {
        return type.getSimpleName();
    }

    public String getFullClassName() {
        return type.getName();
    }

    public boolean isImport() {
        String fullClassName = getFullClassName();
        return !type.isPrimitive() && !"java.lang".equals(
                StringUtils.substringBeforeLast(fullClassName, ".")) && !"byte[]".equals(type.getSimpleName());
    }
}
