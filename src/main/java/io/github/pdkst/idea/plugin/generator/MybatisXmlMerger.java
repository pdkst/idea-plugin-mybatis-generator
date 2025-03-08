package io.github.pdkst.idea.plugin.generator;

import io.github.pdkst.idea.plugin.common.pojo.MybatisMethod;
import io.github.pdkst.idea.plugin.common.pojo.MybatisXml;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

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
    private static final String[] DEFAULT_METHOD_NAMES = new String[]{"BaseResultMap", "Example_Where_Clause",
            "Update_By_Example_Where_Clause", "Base_Column_List", "selectByExampleWithBLOBs", "selectByExample",
            "selectByPrimaryKey", "deleteByPrimaryKey", "deleteByExample", "insert", "insertSelective",
            "updateByPrimaryKeySelective", "updateByPrimaryKeyWithBLOBs", "updateByPrimaryKey",
            "updateByExampleSelective", "updateByExampleWithBLOBs", "updateByExample", "countByExample",
            "countByPrimaryKey"};

    public static MybatisXml parse(String xmlFile) {
        try {// 解析xml文件
            final Document document = parseDocument(xmlFile);
            if (document == null) {
                return null;
            }
            // 获取根元素
            Element rootElement = document.getRootElement();
            final List<MybatisMethod> methods = new ArrayList<>();
            for (Element element : rootElement.elements()) {
                final MybatisMethod mybatisMethod = parseXmlMethod(element);
                if (mybatisMethod == null) {
                    continue;
                }
                methods.add(mybatisMethod);
            }
            return new MybatisXml(methods);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static MybatisMethod parseXmlMethod(Element element) {
        final String id = element.attributeValue("id");
        final MybatisMethod mybatisMethod = new MybatisMethod();
        mybatisMethod.setDefaultMethod(isDefaultMethod(id));
        mybatisMethod.setId(id);
        mybatisMethod.setElement(element.createCopy());
        return mybatisMethod;
    }

    private static boolean isDefaultMethod(String id) {
        for (String defaultMethodName : DEFAULT_METHOD_NAMES) {
            if (defaultMethodName.equals(id)) {
                return true;
            }
        }
        return false;
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
        for (MybatisMethod mybatisMethod : mybatisXml) {
            if (mybatisMethod.isDefaultMethod()) {
                continue;
            }
            rootElement.add(mybatisMethod.getElement());
        }
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
