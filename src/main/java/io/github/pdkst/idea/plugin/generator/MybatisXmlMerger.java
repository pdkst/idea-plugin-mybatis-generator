package io.github.pdkst.idea.plugin.generator;

import io.github.pdkst.idea.plugin.common.pojo.MybatisMethod;
import io.github.pdkst.idea.plugin.common.pojo.MybatisXml;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pdkst
 * @since 2025/03/08
 */
public class MybatisXmlMerger {

    public static MybatisXml parse(String xmlFile) {
        try {
            // 解析xml文件
            final Document document = parseDocument(xmlFile);
            if (document == null) {
                return null;
            }
            return parse(document);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static MybatisXml parse(Document document) {
        // 获取根元素
        Element rootElement = document.getRootElement();
        final List<MybatisMethod> methods = new ArrayList<>();
        for (Element element : rootElement.elements()) {
            final MybatisMethod mybatisMethod = parseXmlMethod(element);
            methods.add(mybatisMethod);
        }
        return new MybatisXml(methods);
    }

    private static MybatisMethod parseXmlMethod(Element element) {
        final String id = element.attributeValue("id");
        final MybatisMethod mybatisMethod = new MybatisMethod();
        mybatisMethod.setId(id);
        mybatisMethod.setName(element.getName());
        mybatisMethod.setElement(element.createCopy());
        return mybatisMethod;
    }

    public static void merge(String xmlFile, MybatisXml mybatisXml) {
        try {
            if (mybatisXml == null) {
                return;
            }
            final Document document = parseDocument(xmlFile);
            if (document == null) {
                return;
            }
            // 获取根元素
            mergeToDocument(mybatisXml, document);
            // 输出合并后的 XML 文档
            writeDocument(document, xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeDocument(Document document, String xmlFile) throws IOException {
        // 将合并后的 XML 写入文件
        XMLWriter writer = new XMLWriter(new FileWriter(xmlFile));
        writer.write(document);
        writer.close();
    }

    private static void mergeToDocument(MybatisXml mybatisXml, Document document) {
        Element rootElement = document.getRootElement();
        MybatisXml targetXml = parse(document);
        for (MybatisMethod mybatisMethod : mybatisXml) {
            if (targetXml.hasMethod(mybatisMethod.getId())) {
                continue;
            }
            // 添加换行
            rootElement.add(new DefaultText("\n  "));
            rootElement.add(mybatisMethod.getElement());
        }
        rootElement.add(new DefaultText("\n"));
    }

    private static Document parseDocument(String xmlFile) throws DocumentException {
        // 解析xml文件
        final File file = new File(xmlFile);
        if (!file.exists()) {
            return null;
        }
        // 创建 SAXReader 对象
        SAXReader reader = new SAXReader();
        // 读取第一个 XML 文件
        return reader.read(file);
    }
}
