package io.github.pdkst.idea.plugin.generator.engin;

import java.util.Map;

/**
 * @author pdkst
 * @since 2025/8/31
 */
public interface TemplateEngine {
    /**
     * 生成文件
     *
     * @param objectMap    模板参数
     * @param templatePath 模板路径
     * @param outputFile   生成文件
     */
    void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception;
}
