<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>

<@c.page>

<#if Session?? && Session.SPRING_SECURITY_LAST_EXCEPTION??>
    <div class="alert alert-danger" role="alert">
        ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
    </div>
</#if>

<#if post??>
    <div class="alert alert-${mode!'primary'}" role="alert">
        ${post}
    </div>
</#if>

<@l.login "/login" false/>

</@c.page>