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
<#if mapperPackage?default("")?trim?length gt 1>
import ${mapperFullClassName};
</#if>
import ${superFacadeImplClass};
</#if>
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务实现类
 *
 * @author ${author!}
 * @since ${.now?string("yyyy-MM-dd HH:mm")}
 */
@Service
<#if superFacadeImplClass?? && superFacadeImplClass !="">
public class ${facadeImplName} extends ${superFacadeImplClassName}<${mapperName}, ${entityName}> implements ${facadeName} {
<#else>
public class ${facadeImplName} implements ${facadeName} {
</#if>

}
