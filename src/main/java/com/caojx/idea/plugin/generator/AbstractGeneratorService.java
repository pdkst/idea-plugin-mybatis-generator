package com.caojx.idea.plugin.generator;

import com.caojx.idea.plugin.common.constants.Constant;
import com.caojx.idea.plugin.common.pojo.TableInfo;
import com.caojx.idea.plugin.common.properties.*;
import com.caojx.idea.plugin.common.utils.ClassUtils;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.caojx.idea.plugin.generator.engin.FreemarkerTemplateEngine;
import com.google.common.base.CaseFormat;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.pdkst.idea.plugin.common.pojo.MybatisXml;
import io.github.pdkst.idea.plugin.generator.MybatisXmlMerger;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 生成文件抽象类
 *
 * @author caojx
 * @date 2022/4/4
 */
public abstract class AbstractGeneratorService implements IGeneratorService {

    private final FreemarkerTemplateEngine freemarkerTemplateEngine = new FreemarkerTemplateEngine();

    @Override
    public void doGenerator(Project project, GeneratorContext generatorContext) {
        // 生成配置信息
        GeneratorProperties generatorProperties = generatorContext.getGeneratorProperties();

        for (TableInfo table : generatorContext.getTables()) {

            // 获取模板数据
            Map<String, Object> objectMap = getObjectMap(generatorContext, table);

            // entity
            EntityProperties entityProperties = generatorProperties.getEntityProperties();
            if (entityProperties.isSelectedGenerateCheckBox()) {
                String entityFile = entityProperties.getPath() + File.separator + objectMap.get("entityName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("entityTemplatePath").toString(), entityFile);
            }

            // entityExample
            if (entityProperties.isSelectedGenerateEntityExampleCheckBox()) {
                String entityExampleFile = entityProperties.getPath() + File.separator + objectMap.get("entityExampleName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("entityExampleTemplatePath").toString(), entityExampleFile);
            }

            // mapper
            MapperProperties mapperProperties = generatorProperties.getMapperProperties();
            if (mapperProperties.isSelectedGenerateCheckBox()) {
                String mapperFile = mapperProperties.getPath() + File.separator + objectMap.get("mapperName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("mapperTemplatePath").toString(), mapperFile);
            }

            // mapperXml
            MapperXmlProperties mapperXmlProperties = generatorProperties.getMapperXmlProperties();
            if (mapperXmlProperties.isSelectedGenerateCheckBox()) {
                String mapperXmlFile = mapperXmlProperties.getPath() + File.separator + objectMap.get("mapperXmlName") + Constant.XML_SUFFIX;
                final MybatisXml mybatisXml = MybatisXmlMerger.parse(mapperXmlFile);
                generatorFile(project, objectMap, objectMap.get("mapperXmlTemplatePath").toString(), mapperXmlFile);
                // 合并xml
                if (mybatisXml != null) {
                    MybatisXmlMerger.merge(mapperXmlFile, mybatisXml);
                    refreshVirtualFile(mapperXmlFile);
                }
            }

            // service
            ServiceProperties serviceProperties = generatorProperties.getServiceProperties();
            if (serviceProperties.isSelectedGenerateCheckBox()) {
                String serviceFile = serviceProperties.getPath() + File.separator + objectMap.get("serviceName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("serviceTemplatePath").toString(), serviceFile);
            }

            // serviceImpl
            ServiceImplProperties serviceImplProperties = generatorProperties.getServiceImplProperties();
            if (serviceImplProperties.isSelectedGenerateCheckBox()) {
                String serviceImplFile = serviceImplProperties.getPath() + File.separator + objectMap.get("serviceImplName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("serviceImplTemplatePath").toString(), serviceImplFile);
            }

            // facade
            FacadeProperties facadeProperties = generatorProperties.getFacadeProperties();
            if (facadeProperties.isSelectedGenerateCheckBox()) {
                String serviceFile = facadeProperties.getPath() + File.separator + objectMap.get("facadeName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("facadeTemplatePath").toString(), serviceFile);
            }

            // facadeImpl
            FacadeImplProperties facadeImplProperties = generatorProperties.getFacadeImplProperties();
            if (facadeImplProperties.isSelectedGenerateCheckBox()) {
                String serviceImplFile = facadeImplProperties.getPath() + File.separator + objectMap.get("facadeImplName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("facadeImplTemplatePath").toString(), serviceImplFile);
            }

            // controller
            ControllerProperties controllerProperties = generatorProperties.getControllerProperties();
            if (controllerProperties.isSelectedGenerateCheckBox()) {
                String controllerFile = controllerProperties.getPath() + File.separator + objectMap.get("controllerName") + Constant.JAVA_SUFFIX;
                generatorFile(project, objectMap, objectMap.get("controllerTemplatePath").toString(), controllerFile);
            }
        }
    }

    /**
     * 模板数据
     *
     * @param generatorContext 上线文信息
     * @param tableInfo        表信息
     * @return 模板数据
     */
    public Map<String, Object> getObjectMap(GeneratorContext generatorContext, TableInfo tableInfo) {
        Map<String, Object> objectMap = new HashMap<>();

        GeneratorProperties generatorProperties = generatorContext.getGeneratorProperties();

        // 表名转基础驼峰
        String tableName = tableInfo.getName();
        // 移除表名前缀
        final String tableNamePrefix = generatorProperties.getCommonProperties().getTableNamePrefix();
        if (StringUtils.isNotBlank(tableNamePrefix)) {
            tableName = StringUtils.removeStartIgnoreCase(tableName, tableNamePrefix);
            tableName = StringUtils.removeStartIgnoreCase(tableName, "_");
        }
        String baseEntityName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName);

        // 表信息
        objectMap.put("table", tableInfo);

        // 公共配置
        CommonProperties commonProperties = generatorProperties.getCommonProperties();
        objectMap.put("author", commonProperties.getAuthor());
        objectMap.put("frameworkType", commonProperties.getFrameworkTypeComboBoxValue());

        // entity
        EntityProperties entityProperties = generatorProperties.getEntityProperties();
        String entityName = String.format(entityProperties.getNamePattern(), baseEntityName);
        objectMap.put("entityTemplatePath", Constant.ENTITY_TEMPLATE_PATH);
        objectMap.put("entityPackage", entityProperties.getPackageName());
        objectMap.put("entityName", entityName);
        objectMap.put("entityFullClassName", ClassUtils.getFullClassName(entityProperties.getPackageName(), entityName));
        objectMap.put("entityImportPackages", tableInfo.getImportPackages());
        objectMap.put("isSelectedSerializableCheckBox", entityProperties.isSelectedSerializableCheckBox());
        objectMap.put("isSelectedDataCheckBox", entityProperties.isSelectedDataCheckBox());
        objectMap.put("isSelectedBuilderCheckBox", entityProperties.isSelectedBuilderCheckBox());
        objectMap.put("isSelectedNoArgsConstructorCheckBox", entityProperties.isSelectedNoArgsConstructorCheckBox());
        objectMap.put("isSelectedAllArgsConstructorCheckBox", entityProperties.isSelectedAllArgsConstructorCheckBox());
        objectMap.put("isGenerateGetterSetter", isGenerateGetterSetter(entityProperties));
        objectMap.put("isSelectedEntitySwaggerCheckBox", entityProperties.isSelectedSwaggerCheckBox());

        // entityExample
        String entityExampleName = String.format(entityProperties.getExampleNamePattern(), baseEntityName);
        objectMap.put("entityExampleTemplatePath", Constant.ENTITY_EXAMPLE_TEMPLATE_PATH);
        objectMap.put("entityExamplePackage", entityProperties.getPackageName());
        objectMap.put("entityExampleName", entityExampleName);
        objectMap.put("entityExampleFullClassName", ClassUtils.getFullClassName(entityProperties.getPackageName(), entityExampleName));

        // mapper
        MapperProperties mapperProperties = generatorProperties.getMapperProperties();
        String mapperName = String.format(mapperProperties.getNamePattern(), baseEntityName);
        objectMap.put("mapperTemplatePath", Constant.MAPPER_TEMPLATE_PATH);
        objectMap.put("mapperPackage", mapperProperties.getPackageName());
        objectMap.put("mapperName", mapperName);
        objectMap.put("mapperInstanceName", StringUtils.uncapitalize(mapperName));
        objectMap.put("mapperFullClassName", ClassUtils.getFullClassName(mapperProperties.getPackageName(), mapperName));
        objectMap.put("superMapperClass", mapperProperties.getSuperMapperClass());
        objectMap.put("superMapperClassName", ClassUtils.getClassNameByFullClassName(mapperProperties.getSuperMapperClass()));
        objectMap.put("superMapperClassPackage", ClassUtils.getPackageNameByFullClassName(mapperProperties.getSuperMapperClass()));
        objectMap.put("isSelectedEnableInsertCheckBox", mapperProperties.isSelectedEnableInsertCheckBox());
        objectMap.put("isSelectedEnableSelectByPrimaryKeyCheckBox", mapperProperties.isSelectedEnableSelectByPrimaryKeyCheckBox());
        objectMap.put("isSelectedEnableSelectByExampleCheckBox", mapperProperties.isSelectedEnableSelectByExampleCheckBox());
        objectMap.put("isSelectedEnableUpdateByPrimaryKeyCheckBox", mapperProperties.isSelectedEnableUpdateByPrimaryKeyCheckBox());
        objectMap.put("isSelectedEnableUpdateByExampleCheckBox", mapperProperties.isSelectedEnableUpdateByExampleCheckBox());
        objectMap.put("isSelectedEnableDeleteByPrimaryKeyCheckBox", mapperProperties.isSelectedEnableDeleteByPrimaryKeyCheckBox());
        objectMap.put("isSelectedEnableDeleteByExampleCheckBox", mapperProperties.isSelectedEnableDeleteByExampleCheckBox());
        objectMap.put("isSelectedEnableCountByExampleCheckBox", mapperProperties.isSelectedEnableCountByExampleCheckBox());

        // mapperXml
        MapperXmlProperties mapperXmlProperties = generatorProperties.getMapperXmlProperties();
        String mapperXmlName = String.format(mapperXmlProperties.getNamePattern(), baseEntityName);
        objectMap.put("mapperXmlTemplatePath", Constant.MAPPER_XML_TEMPLATE_PATH);
        objectMap.put("mapperXmlName", mapperXmlName);

        // service
        ServiceProperties serviceProperties = generatorProperties.getServiceProperties();
        String serviceName = String.format(serviceProperties.getNamePattern(), baseEntityName);
        objectMap.put("serviceTemplatePath", Constant.SERVICE_TEMPLATE_PATH);
        objectMap.put("servicePackage", serviceProperties.getPackageName());
        objectMap.put("serviceName", serviceName);
        objectMap.put("serviceInstanceName", StringUtils.uncapitalize(serviceName));
        objectMap.put("serviceFullClassName", ClassUtils.getFullClassName(serviceProperties.getPackageName(), serviceName));
        objectMap.put("superServiceClass", serviceProperties.getSuperServiceClass());
        objectMap.put("superServiceClassName", ClassUtils.getClassNameByFullClassName(serviceProperties.getSuperServiceClass()));
        objectMap.put("superServiceClassPackage", ClassUtils.getPackageNameByFullClassName(serviceProperties.getSuperServiceClass()));

        // serviceImpl
        ServiceImplProperties serviceImplProperties = generatorProperties.getServiceImplProperties();
        String serviceImplName = String.format(serviceImplProperties.getNamePattern(), baseEntityName);
        objectMap.put("serviceImplTemplatePath", Constant.SERVICE_IMPL_TEMPLATE_PATH);
        objectMap.put("serviceImplPackage", serviceImplProperties.getPackageName());
        objectMap.put("serviceImplName", serviceImplName);
        objectMap.put("serviceImplFullClassName", ClassUtils.getFullClassName(serviceImplProperties.getPackageName(), serviceImplName));
        objectMap.put("superServiceImplClass", serviceImplProperties.getSuperServiceImplClass());
        objectMap.put("superServiceImplClassName", ClassUtils.getClassNameByFullClassName(serviceImplProperties.getSuperServiceImplClass()));
        objectMap.put("superServiceImplClassPackage", ClassUtils.getPackageNameByFullClassName(serviceImplProperties.getSuperServiceImplClass()));


        // facade
        FacadeProperties facadeProperties = generatorProperties.getFacadeProperties();
        String facadeName = String.format(facadeProperties.getNamePattern(), baseEntityName);
        objectMap.put("facadeTemplatePath", Constant.FACADE_TEMPLATE_PATH);
        objectMap.put("facadePackage", facadeProperties.getPackageName());
        objectMap.put("facadeName", facadeName);
        objectMap.put("facadeInstanceName", StringUtils.uncapitalize(facadeName));
        objectMap.put("facadeFullClassName", ClassUtils.getFullClassName(facadeProperties.getPackageName(), facadeName));
        objectMap.put("superFacadeClass", facadeProperties.getSuperClass());
        objectMap.put("superFacadeClassName", ClassUtils.getClassNameByFullClassName(facadeProperties.getSuperClass()));
        objectMap.put("superFacadeClassPackage", ClassUtils.getPackageNameByFullClassName(facadeProperties.getSuperClass()));

        // facadeImpl
        FacadeImplProperties facadeImplProperties = generatorProperties.getFacadeImplProperties();
        String facadeImplName = String.format(facadeImplProperties.getNamePattern(), baseEntityName);
        objectMap.put("facadeImplTemplatePath", Constant.FACADE_IMPL_TEMPLATE_PATH);
        objectMap.put("facadeImplPackage", facadeImplProperties.getPackageName());
        objectMap.put("facadeImplName", facadeImplName);
        objectMap.put("facadeImplFullClassName", ClassUtils.getFullClassName(facadeImplProperties.getPackageName(), facadeImplName));
        objectMap.put("superFacadeImplClass", facadeImplProperties.getSuperClass());
        objectMap.put("superFacadeImplClassName", ClassUtils.getClassNameByFullClassName(facadeImplProperties.getSuperClass()));
        objectMap.put("superFacadeImplClassPackage", ClassUtils.getPackageNameByFullClassName(facadeImplProperties.getSuperClass()));

        // controller
        ControllerProperties controllerProperties = generatorProperties.getControllerProperties();
        String controllerName = String.format(controllerProperties.getNamePattern(), baseEntityName);
        objectMap.put("controllerTemplatePath", Constant.CONTROLLER_TEMPLATE_PATH);
        objectMap.put("controllerPackage", controllerProperties.getPackageName());
        objectMap.put("controllerName", controllerName);
        objectMap.put("controllerFullClassName", ClassUtils.getFullClassName(controllerProperties.getPackageName(), controllerName));
        objectMap.put("isSelectedSwaggerCheckBox", controllerProperties.isSelectedSwaggerCheckBox());
        objectMap.put("controllerMappingHyphen", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, tableName));
        return objectMap;
    }

    /**
     * 生成文件
     *
     * @param project      项目
     * @param objectMap    模板参数
     * @param templatePath 模板路径
     * @param outputFile   生成文件
     */
    public void generatorFile(Project project, Map<String, Object> objectMap, String templatePath, String outputFile) {
        try {
            // 生成文件
            freemarkerTemplateEngine.writer(objectMap, templatePath, outputFile);

            refreshVirtualFile(outputFile);
        } catch (Exception e) {
            MyMessages.showErrorNotify(project, outputFile + "文件生成失败" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void refreshVirtualFile(String outputFile) {
        // 刷新文件
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(outputFile));
        if (Objects.nonNull(virtualFile)) {
            virtualFile.refresh(false, true);
        }
    }

    /**
     * 是否生成GetterSetter
     *
     * @param entityProperties entity配置
     * @return true 生成、false 不生成
     */
    private boolean isGenerateGetterSetter(EntityProperties entityProperties) {
        return !entityProperties.isSelectedDataCheckBox();
    }

    /**
     * 校验生成数据
     *
     * @param generatorProperties 生成配置
     * @return 返回非空字符串，校验不通过
     */
    public static String validGeneratorData(GeneratorProperties generatorProperties) {
        // 公共配置校验
        CommonProperties commonProperties = generatorProperties.getCommonProperties();
        if (StringUtils.isBlank(commonProperties.getAuthor())) {
            return "请填写生成作者";
        }

        // 实体校验
        EntityProperties entityProperties = generatorProperties.getEntityProperties();
        if (StringUtils.isBlank(entityProperties.getPath())) {
            return "请选择entity路径";
        }
        if (!validNamePattern(entityProperties.getNamePattern())) {
            return "entity命名格式需要包含%s";
        }

        // mapper校验
        MapperProperties mapperProperties = generatorProperties.getMapperProperties();
        if (mapperProperties.isSelectedGenerateCheckBox()) {
            if (StringUtils.isBlank(mapperProperties.getPath())) {
                return "请选择mapper路径";
            }
//            if (StringUtils.isBlank(mapperProperties.getPackageName())) {
//                return "请填写mapper包名";
//            }
            if (!validNamePattern(mapperProperties.getNamePattern())) {
                return "mapper命名格式需要包含%s";
            }
        }

        // mapperXml校验
        MapperXmlProperties mapperXmlProperties = generatorProperties.getMapperXmlProperties();
        if (mapperXmlProperties.isSelectedGenerateCheckBox()) {
            if (StringUtils.isBlank(mapperXmlProperties.getPath())) {
                return "请选择mapperXml路径";
            }
            if (!validNamePattern(mapperXmlProperties.getNamePattern())) {
                return "mapperXml命名格式需要包含%s";
            }

//            // 生成mapperXml依赖的其他填写项
//            if (StringUtils.isBlank(mapperProperties.getPackageName())) {
//                return "生成mapperXml，请填写mapper包名";
//            }
            if (!validNamePattern(mapperProperties.getNamePattern())) {
                return "生成mapperXml，mapper命名格式需要包含%s";
            }
        }

        // service校验
        ServiceProperties serviceProperties = generatorProperties.getServiceProperties();
        if (serviceProperties.isSelectedGenerateCheckBox()) {
            if (StringUtils.isBlank(serviceProperties.getPath())) {
                return "请选择service路径";
            }
//            if (StringUtils.isBlank(serviceProperties.getPackageName())) {
//                return "请填写service包名";
//            }
            if (!validNamePattern(serviceProperties.getNamePattern())) {
                return "service命名格式需要包含%s";
            }
        }

        // serviceImpl校验
        ServiceImplProperties serviceImplProperties = generatorProperties.getServiceImplProperties();
        if (serviceImplProperties.isSelectedGenerateCheckBox()) {
            if (StringUtils.isBlank(serviceImplProperties.getPath())) {
                return "请选择serviceImpl路径";
            }
//            if (StringUtils.isBlank(serviceImplProperties.getPackageName())) {
//                return "请填写serviceImpl包名";
//            }
            if (!validNamePattern(serviceImplProperties.getNamePattern())) {
                return "serviceImpl命名格式需要包含%s";
            }

            // 生成serviceImpl依赖的其他填写项
//            if (StringUtils.isBlank(serviceProperties.getPackageName())) {
//                return "生成serviceImpl，请填写service包名";
//            }
            if (!validNamePattern(serviceProperties.getNamePattern())) {
                return "生成serviceImpl，service命名格式需要包含%s";
            }
            if (StringUtils.isNotBlank(serviceImplProperties.getSuperServiceImplClass())) {
//                if (StringUtils.isBlank(mapperProperties.getPackageName())) {
//                    return "生成serviceImpl，请填写mapper包名";
//                }
                if (!validNamePattern(mapperProperties.getNamePattern())) {
                    return "生成serviceImpl，mapper命名格式需要包含%s";
                }
            }
        }

        // controller
        ControllerProperties controllerProperties = generatorProperties.getControllerProperties();
        if (controllerProperties.isSelectedGenerateCheckBox()) {
            if (StringUtils.isBlank(controllerProperties.getPath())) {
                return "请选择controller路径";
            }
//            if (StringUtils.isBlank(controllerProperties.getPackageName())) {
//                return "请填写controller包名";
//            }
            if (!validNamePattern(controllerProperties.getNamePattern())) {
                return "controller命名格式需要包含%s";
            }
        }
        FacadeProperties facadeProperties = generatorProperties.getFacadeProperties();
        FacadeImplProperties facadeImplProperties = generatorProperties.getFacadeImplProperties();

        // 至少要生成一种文件
        if (!(entityProperties.isSelectedGenerateCheckBox() || entityProperties.isSelectedGenerateEntityExampleCheckBox() || mapperProperties.isSelectedGenerateCheckBox() || mapperXmlProperties.isSelectedGenerateCheckBox() || serviceProperties.isSelectedGenerateCheckBox() || serviceImplProperties.isSelectedGenerateCheckBox() || controllerProperties.isSelectedGenerateCheckBox() || facadeProperties.isSelectedGenerateCheckBox() || facadeImplProperties.isSelectedGenerateCheckBox())) {
            return "至少要选择生成一种文件";
        }
        return "";
    }

    /**
     * 校验命名格式
     *
     * @param namePattern 命名格式
     * @return true 校验通过、false 校验失败
     */
    private static boolean validNamePattern(String namePattern) {
        return StringUtils.isNotBlank(namePattern) && namePattern.contains("%s");
    }
}
