package io.github.pdkst.idea.plugin.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据库类型枚举
 *
 * @author caojx
 * @since 2022/4/10 4:00 PM
 */
@Getter
@RequiredArgsConstructor
public enum DataBaseTypeEnum {

    /**
     * mysql
     */
    MySQL("mysql"),
    /**
     * mariadb
     */
    MariaDb("mariadb"),

//    Oracle,

    ;

    private final String databaseType;

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
        return MySQL;
    }

}
