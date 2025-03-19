package io.github.pdkst.idea.plugin.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author pdkst
 * @since 2025/03/08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MybatisXml implements Iterable<MybatisMethod> {
    @Delegate
    private List<MybatisMethod> methods = new ArrayList<>();

    public boolean hasMethod(String id) {
        for (MybatisMethod method : methods) {
            if (Objects.equals(method.getId(), id)) {
                return true;
            }
        }
        return false;
    }
}
