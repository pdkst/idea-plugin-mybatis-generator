<#if facadePackage?default("")?trim?length gt 1>
package ${facadePackage};

</#if>
<#if superFacadeClass?? && superFacadeClass !="">
<#if entityPackage?default("")?trim?length gt 1>
import ${entityFullClassName};
</#if>
import ${superFacadeClass};
</#if>

/**
 * ${table.comment!} 服务类接口
 *
 * @author ${author!}
 * @since ${.now?string("yyyy-MM-dd HH:mm")}
 */
<#if superFacadeClass?? && superFacadeClass !="" >
public interface ${facadeName} extends ${superFacadeClassName}<${entityName}> {
<#else>
public interface ${facadeName} {
</#if>

}
