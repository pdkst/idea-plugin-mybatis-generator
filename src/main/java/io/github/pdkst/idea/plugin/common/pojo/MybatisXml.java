package io.github.pdkst.idea.plugin.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

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
}
