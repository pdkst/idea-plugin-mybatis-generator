package io.github.pdkst.idea.plugin.generator;

import io.github.pdkst.idea.plugin.common.pojo.MybatisMethod;
import io.github.pdkst.idea.plugin.common.pojo.MybatisXml;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis XML文件合并工具类，使用JDOM2替代dom4j实现XML解析与生成
 *
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
        } catch (Throwable e) {
            if (e instanceof ExceptionInInitializerError) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static MybatisXml parse(Document document) {
        // 获取根元素
        Element rootElement = document.getRootElement();
        final List<MybatisMethod> methods = new ArrayList<>();
        for (Element element : rootElement.getChildren()) {
            final MybatisMethod mybatisMethod = parseXmlMethod(element);
            methods.add(mybatisMethod);
        }
        return new MybatisXml(methods);
    }

    private static MybatisMethod parseXmlMethod(Element element) {
        final String id = element.getAttributeValue("id");
        final MybatisMethod mybatisMethod = new MybatisMethod();
        mybatisMethod.setId(id);
        mybatisMethod.setName(element.getName());
        mybatisMethod.setElement((Element) element.clone());
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
        // 将合并后的 XML 写入文件，使用格式化输出保留缩进
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(document, new FileWriter(xmlFile));
    }

    private static void mergeToDocument(MybatisXml mybatisXml, Document document) {
        Element rootElement = document.getRootElement();
        MybatisXml targetXml = parse(document);
        for (MybatisMethod mybatisMethod : mybatisXml) {
            if (targetXml.hasMethod(mybatisMethod.getId())) {
                continue;
            }
            // 添加新元素前先添加换行和缩进
            rootElement.addContent("\n  ");
            rootElement.addContent((Element) mybatisMethod.getElement().clone());
        }
        rootElement.addContent("\n");
    }

    private static Document parseDocument(String xmlFile) throws JDOMException, IOException {
        // 解析xml文件
        final File file = new File(xmlFile);
        if (!file.exists()) {
            return null;
        }
        // 创建 SAXBuilder 对象解析XML
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(file);
    }
}
