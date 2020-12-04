<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>
<#import "parts/security.ftlh" as sec>

<@c.page>

<!-- заголовок -имя пользователя -->
<div class="container">
    <h3>${user.username}
        <!--если аккаунт активирован, то показан значок об этом-->
        <#if user.active>
            <span class="badge badge-secondary">activated</span>
        </#if>
    </h3>
</div>

<div class="container">
    <#list user.roles as role>
        <h4><span class="badge badge-info">${role}</span></h4>
    </#list>
</div>

<#if user.messages??>
    <#include "parts/messageList.ftlh" />
<#else>
    No posts yet!
</#if>


</@c.page>