<#if facadeImplPackage?default("")?trim?length gt 1>
package ${facadeImplPackage};

</#if>
<#if facadePackage?default("")?trim?length gt 1>
import ${facadeFullClassName};
</#if>
<#if superFacadeImplClass?? && superFacadeImplClass !="">
<#if entityPackage?default("")?trim?length gt 1>
import ${entityFullClassName};
</#if>
import ${superFacadeImplClass};
</#if>
import ${serviceFullClassName};
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ${table.comment!} 服务实现类
 *
 * @author ${author!}
 * @since ${.now?string("yyyy-MM-dd HH:mm")}
 */
@Slf4j
@Component
@RequiredArgsConstructor
<#if superFacadeImplClass?? && superFacadeImplClass !="">
public class ${facadeImplName} extends ${superFacadeImplClassName}<${mapperName}, ${entityName}> implements ${facadeName} {
<#else>
public class ${facadeImplName} implements ${facadeName} {
</#if>
    private final ${serviceName} ${serviceInstanceName};

}
