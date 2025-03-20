<#if serviceImplPackage?default("")?trim?length gt 1>
package ${serviceImplPackage};

</#if>
<#if servicePackage?default("")?trim?length gt 1>
import ${serviceFullClassName};
</#if>
<#if superServiceImplClass?? && superServiceImplClass !="">
<#if entityPackage?default("")?trim?length gt 1>
import ${entityFullClassName};
</#if>
import ${superServiceImplClass};
</#if>
import ${mapperFullClassName};
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务实现类
 *
 * @author ${author!}
 * @since ${.now?string("yyyy-MM-dd HH:mm")}
 */
@Slf4j
@Service
@RequiredArgsConstructor
<#if superServiceImplClass?? && superServiceImplClass !="">
public class ${serviceImplName} extends ${superServiceImplClassName}<${mapperName}, ${entityName}> implements ${serviceName} {
<#else>
public class ${serviceImplName} implements ${serviceName} {
</#if>
    private final ${mapperName} ${mapperInstanceName};

}
