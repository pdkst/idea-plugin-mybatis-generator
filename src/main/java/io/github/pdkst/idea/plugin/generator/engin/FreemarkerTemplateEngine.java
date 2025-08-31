package io.github.pdkst.idea.plugin.generator.engin;

import com.caojx.idea.plugin.common.constants.Constant;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * freemarker模板引擎
 *
 * @author caojx
 * @since 2022/4/16 10:59 AM
 */
public class FreemarkerTemplateEngine implements TemplateEngine {

    private final Configuration configuration;

    public FreemarkerTemplateEngine() {
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding(Constant.UTF8);
        configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, Constant.SLASH);
    }
    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        // 上级目录不存在，创建目录
        File file = new File(outputFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // 生成文件
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            Template template = configuration.getTemplate(templatePath);
            template.process(objectMap, new OutputStreamWriter(fileOutputStream, Constant.UTF8));
        }
    }
}
