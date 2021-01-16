<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>
<#import "parts/security.ftlh" as sec>
<#import "parts/postsList.ftlh" as pl>
<#import "parts/pager.ftlh" as pager>
<#import "parts/post.ftlh" as p>



<!--
@RequestParams:
User user
-->

<@c.page>


<div class="container">
    <div class="row mb-2">
        <div class="col">
            <!-- заголовок -имя пользователя -->
            <div class="pt-2">
                <h3>${user.username}
                    <!--если аккаунт активирован, то показан значок об этом-->
                    <#if user.active>
                        <span class="badge badge-secondary">activated</span>
                    </#if>
                </h3>
            </div>
        </div>
        <!-- подписчики-->
        <div class="col-md-auto pt-2">
            <a href="/subscribers?id=${user.id}"  type="button" class="btn btn-primary">
                Подписчики <span class="badge badge-light">${user.getAmountOfSubscribers()}</span>
            </a>
        </div>

        <!-- подписки-->
        <div class="col-md-auto pt-2">
            <a href="/subscriptions?id=${user.id}"  type="button" class="btn btn-primary">
                Подписки <span class="badge badge-light">${user.getAmountOfSubscriptions()}</span>
            </a>
        </div>
    </div>
    <div class="row">
        <#if user.ava??>
            <img width="150" src="${hostUrl}/img/avas/${user.ava}"/>
        <#else>
            <img width="150" src="${hostUrl}/img/avas/default.png"/>
        </#if>
    </div>
</div>





<!-- список ролей -->
<div class="mb-2">
    <#list user.roles as role>
        <h4><span class="badge badge-info">${role}</span></h4>
    </#list>
</div>

<!-- кнопка подписаться -->
<#if !user.hasSubscriber(sec.user.id) && sec.user.getId() != user.id>
<div class="mb-2">
    <form action="/subscribe?id=${user.id}" method="post">
        <button class="btn btn-info">Подписаться</button>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>
</div>
<!-- или отписаться -->
<#elseif sec.user.getId() != user.id>
<div class="mb-2">
    <form action="/unsubscribe?id=${user.id}" method="post">
        <button class="btn btn-warning">Отписаться</button>
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>
</div>
</#if>

<#if user.getListOfPosts()??>

    <#assign url = "/profiles/${user.id}"/>
    <@pager.pager url!'/' page/>

    <!-- Кнопка для прогрузки предыдущих постов, если они есть -->
    <#if (page.hasPrevious()) && (!tagFilter?has_content) && (!textFilter?has_content)>
        <a class="btn btn-outline-warning btn-lg btn-block my-3" href="/profiles/${user.id}?page=${page.getNumber() - 1}&size=${page.getSize()}" type="submit">
            Загрузить предыдущие посты
        </a>
    </#if>


    <@pl.listOfPosts page.getContent() user/>

    <!-- Кнопка для прогрузки еще постов -->
    <#if (!tagFilter?has_content) && (!textFilter?has_content) && (page.hasNext())>
        <a class="btn btn-outline-warning btn-lg btn-block my-3" href="/profiles/${user.id}?page=${page.getNumber() + 1}&size=${page.getSize()}" type="submit">
            Загрузить еще посты
        </a>
    </#if>

    <@pager.pager url!'/' page/>

<#else>
    No posts yet!
</#if>


</@c.page>