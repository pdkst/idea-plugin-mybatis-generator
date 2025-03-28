package com.caojx.idea.plugin.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 数据库类型枚举
 *
 * @author caojx
 * @since 2022/4/10 4:00 PM
 */
@Getter
@RequiredArgsConstructor
public enum DataBaseTypeEnum {

    MYSQL("mysql"),

//    Oracle,

    ;

    private final String databaseType;

    /**
     * 获取数据库类型
     *
     * @return 数据库类型列表
     */
    public static List<String> getDatabaseTypes() {
        List<String> list = new ArrayList<>();
        for (DataBaseTypeEnum dataBaseTypeEnum : DataBaseTypeEnum.values()) {
            list.add(dataBaseTypeEnum.name());
        }
        return list;
    }

    /**
     * 根据数据库类型获取枚举
     *
     * @param databaseType 数据库类型
     * @return 枚举
     */
    public static DataBaseTypeEnum getEnumByDatabaseType(String databaseType) {
        for (DataBaseTypeEnum dataBaseTypeEnum : DataBaseTypeEnum.values()) {
            if (StringUtils.equalsIgnoreCase(dataBaseTypeEnum.getDatabaseType(), databaseType)) {
                return dataBaseTypeEnum;
            }
        }
        return null;
    }

}
