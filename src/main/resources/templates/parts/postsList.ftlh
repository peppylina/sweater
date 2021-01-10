<div> Список сообщений </div>
<#list user.getListOfPosts() as post>
<div class="card m-5">
    <#if post.filename??>
        <img src="/img/${post.filename}" class="card-img-top p-2"/>
    </#if>

    <div class="card-body">
        <#if post.tag??>
            <h5 class="card-title">${post.tag}</h5>
        </#if>

        <p class="card-text">${post.getTimeString()}</p>
        <p class="card-text">${post.text!" "}</p>
        <a href="/profiles/${post.authorId}" class="card-link">${post.authorName}</a>

        <#if user.id = sec.curId>
            <form method="get" action="${hostUrl}/posts/edit/${user.id}">
                <button type="submit" class="btn btn-primary m-2">Редактировать</button>
                <input type="hidden" name="post" value="${post.id}"/>
            </form>
        </#if>
    </div>
</div>
<#else>
    No result.
</#list>