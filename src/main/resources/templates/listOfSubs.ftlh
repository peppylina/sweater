<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>
<#import "parts/security.ftlh" as sec>

<!--
Request params:
User user - пользователь, чей список подписок мы хотим посмотреть
isSubscribersPage - true или false - показывает, на какой мы странице
-->


<@c.page>

<h3>List of ${user.username}'s <#if isSubscribersPage>subscribers:<#else>subscriptions:</#if></h3>

<#if isSubscribersPage>
    <#list user.getListOfSubscribers() as sub>
        <div class="container" id="subContainer">
            <div class="row" id="${sub.id}">
                <div class="col-md-auto">
                    <#if sub.ava??>
                        <img width="100" height="100" src="img/avas/${sub.ava}"/>
                    <#else>
                        <img width="100" height="100" src="img/avas/default.png"/>
                    </#if>
                </div>
                <div class="col">
                    <a style="font-size: 24px; color: Orange" href="/profiles/${sub.id}">${sub.username}</a>
                </div>
            </div>
        </div>
    <#else>
        No subscribers!
    </#list>
</#if>

<#if !isSubscribersPage>
    <#list user.getListOfSubscriptions() as sub>
        <div class="container" id="subContainer">
            <div class="row" id="${sub.id}">
                <div class="col-md-auto">
                    <#if sub.ava??>
                        <img width="100" height="100" src="${hostUrl}/img/avas/${sub.ava}"/>
                    <#else>
                        <img width="100" height="100" src="${hostUrl}/img/avas/default.png"/>
                    </#if>
                </div>
                <div class="col">
                    <a style="font-size: 24px; color: Orange" href="/profiles/${sub.id}">${sub.username}</a>
                </div>
            </div>
        </div>
    <#else>
        No subscriptions!
    </#list>
</#if>


</@c.page>