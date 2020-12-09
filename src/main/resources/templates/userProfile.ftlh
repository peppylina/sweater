<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>
<#import "parts/security.ftlh" as sec>



<!--
@RequestParams:
User user

-->

<@c.page>


<div class="container">
    <div class="row">
        <div class="col">
            <!-- заголовок -имя пользователя -->
            <div class="container">
                <h3>${user.username}
                    <!--если аккаунт активирован, то показан значок об этом-->
                    <#if user.active>
                    <span class="badge badge-secondary">activated</span>
                </#if>
                </h3>
            </div>
        </div>
        <!-- подписчики-->
        <div class="col-md-auto">
            <a href="/subscribers?id=${user.id}"  type="button" class="btn btn-primary">
                Подписчики <span class="badge badge-light">${user.getAmountOfSubscribers()}</span>
            </a>
        </div>

        <!-- подписки-->
        <div class="col-md-auto">
            <a href="/subscriptions?id=${user.id}"  type="button" class="btn btn-primary">
                Подписки <span class="badge badge-light">${user.getAmountOfSubscriptions()}</span>
            </a>
        </div>
    </div>
</div>




<!-- список ролей -->
<div class="container">
    <#list user.roles as role>
        <h4><span class="badge badge-info">${role}</span></h4>
    </#list>
</div>

<!-- кнопка подписаться -->
<#if !user.hasSubscriber(sec.user.id) && sec.user.getId() != user.id>
<div class="container">
    <form action="/subscribe?id=${user.id}" method="post">
        <button class="btn btn-primary">Подписаться</button>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>
</div>
<!-- или отписаться -->
<#elseif sec.user.getId() != user.id>
<div class="container">
    <form action="/unsubscribe?id=${user.id}" method="post">
        <button class="btn btn-primary">Отписаться</button>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>
</div>
</#if>

<#if user.getListOfMessages()??>
    <#include "parts/messageList.ftlh" />
<#else>
    No posts yet!
</#if>


</@c.page>