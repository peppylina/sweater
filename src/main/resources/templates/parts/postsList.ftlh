<#macro listOfPosts posts user>
<#list posts as post>
<!--<div class="card m-5" id="postCard">
    <#if post.filename??>
        <img src="/img/${post.filename}" class="card-img-top p-2"/>
    </#if>

    <div class="card-body" id="postBody${post.id}">
        <#if post.tag??>
            <h5 class="card-title">${post.tag}</h5>
        </#if>

        <p class="card-text">${post.getTimeString()}</p>
        <p class="card-text">${post.text!" "}</p>
        <a href="/profiles/${post.authorId}" class="card-link">${post.authorName}</a>

        <#if post.getAuthorId() = sec.curId>
            <form method="get" action="/posts/edit/${user.id}">
                <button type="submit" class="btn btn-primary m-2">Редактировать</button>
                <input type="hidden" name="post" value="${post.id}"/>
            </form>
        </#if>
    </div>
</div>-->
    <@p.post post/>
</#list>
</#macro>