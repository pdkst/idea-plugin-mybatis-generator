package com.caojx.idea.plugin.common.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表模型
 *
 * @author caojx
 * @date 2022/4/10 4:00 PM
 */
@Data
public class TableInfo implements Serializable {

    /**
     * 表名
     */
    private String name;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 属性列表
     */
    private List<TableField> fields;

    /**
     * 是否有主键
     */
    private boolean havePrimaryKey;

    /**
     * 主键id名称
     */
    private String primaryKeyName;

    /**
     * 主键类型
     */
    private Class<?> primaryKeyType;

    /**
     * 是否有JdbcDate属性
     */
    private boolean haveJdbcDateField;

    /**
     * 是否有JdbcTime属性
     */
    private boolean haveJdbcTimeField;

    /**
     * 是否有blob属性
     */
    private boolean haveBlobField;

    /**
     * blob属性列表
     */
    private List<TableField> blobFields;

    /**
     * 非blob属性列表
     */
    private List<TableField> notBlobFields;

    /**
     * 构造器
     */
    public TableInfo() {
    }

    /**
     * 构造器
     *
     * @param name    表名
     * @param comment 表注释
     * @param fields  表属性列表
     */
    public TableInfo(String name, String comment, List<TableField> fields) {
        this.name = name;
        this.comment = comment;
        this.fields = fields;

        // 主键类型
        TableField primaryKeyField = Optional.ofNullable(fields).orElse(new ArrayList<>()).stream().filter(TableField::isPrimaryKeyFlag).findAny().orElse(null);
        if (Objects.nonNull(primaryKeyField)) {
            this.havePrimaryKey = true;
            this.primaryKeyName = primaryKeyField.getName();
            this.primaryKeyType = primaryKeyField.getType();
        }

        // 是否含有JdbcDate、JdbcTime属性
        this.haveJdbcDateField = Optional.ofNullable(fields).orElse(new ArrayList<>()).stream().anyMatch(TableField::isJdbcDateFlag);
        this.haveJdbcTimeField = Optional.ofNullable(fields).orElse(new ArrayList<>()).stream().anyMatch(TableField::isJdbcTimeFlag);

        // 是否含有blob属性
        this.blobFields = Optional.ofNullable(fields).orElse(new ArrayList<>()).stream().filter(TableField::isBlobFlag).collect(Collectors.toList());
        this.haveBlobField = !this.blobFields.isEmpty();

        // 非blob属性列表
        this.notBlobFields = Optional.ofNullable(fields).orElse(new ArrayList<>()).stream().filter(field -> !field.isBlobFlag()).collect(Collectors.toList());
    }


    /**
     * 获取实体导包
     *
     * @return 实体导包列表
     */
    public Set<String> getImportPackages() {
        Set<String> imports = new HashSet<>();
        for (TableField field : this.fields) {
            if (field.isImport()) {
                imports.add(field.getFullClassName());
            }
        }
        return imports;
    }
}
