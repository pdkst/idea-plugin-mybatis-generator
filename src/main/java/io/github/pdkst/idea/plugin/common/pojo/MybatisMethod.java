package io.github.pdkst.idea.plugin.common.pojo;

import lombok.Data;
import org.dom4j.Element;

/**
 * @author pdkst
 * @since 2025/03/08
 */
@Data
public class MybatisMethod {
    private boolean defaultMethod;
    private String id;
    private Element element;
}
