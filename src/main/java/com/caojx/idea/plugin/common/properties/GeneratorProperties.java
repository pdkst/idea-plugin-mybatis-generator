package com.caojx.idea.plugin.common.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 生成属性配置
 *
 * @author caojx
 * @date 2022/4/10 12:55 PM
 */
@Data
public class GeneratorProperties implements Serializable {

    /**
     * 公共配置
     */
    private CommonProperties commonProperties = new CommonProperties();

    /**
     * 实体配置
     */
    private EntityProperties entityProperties = new EntityProperties();

    /**
     * mapper配置
     */
    private MapperProperties mapperProperties = new MapperProperties();

    /**
     * mapperXml配置
     */
    private MapperXmlProperties mapperXmlProperties = new MapperXmlProperties();

    /**
     * service配置
     */
    private ServiceProperties serviceProperties = new ServiceProperties();

    /**
     * serviceImpl配置
     */
    private ServiceImplProperties serviceImplProperties = new ServiceImplProperties();

    /**
     * facade配置
     */
    private FacadeProperties facadeProperties = new FacadeProperties();

    /**
     * facadeImpl配置
     */
    private FacadeImplProperties facadeImplProperties = new FacadeImplProperties();

    /**
     * controller配置
     */
    private ControllerProperties controllerProperties = new ControllerProperties();

}
