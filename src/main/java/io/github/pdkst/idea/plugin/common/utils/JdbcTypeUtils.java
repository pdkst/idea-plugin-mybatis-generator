package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.utils.ClassUtils;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.MapUtils;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class JdbcTypeUtils {
    public static Map<JDBCType, Class<?>> toJdbcTypeMap(Map<String, String> jdbcTypeStringMap) {
        // 转换为jdbcType, clazz
        Map<JDBCType, Class<?>> newCustomerJdbcTypeMappingMap = new HashMap<>(4);
        if (MapUtils.isEmpty(newCustomerJdbcTypeMappingMap)) {
            return newCustomerJdbcTypeMappingMap;
        }
        for (Map.Entry<String, String> entry : jdbcTypeStringMap.entrySet()) {
            String jdbcTypeName = entry.getKey();
            String javaTypeName = entry.getValue();
            try {
                newCustomerJdbcTypeMappingMap.put(JDBCType.valueOf(jdbcTypeName), ClassUtils.convertClazz(javaTypeName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newCustomerJdbcTypeMappingMap;
    }
}
