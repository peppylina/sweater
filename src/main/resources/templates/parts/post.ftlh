<#macro post post>
<div class="card mb-4 shadow-sm" id="postCard">

    <div class="media text-muted p-3">
        <img src="/img/avas/${post.author.ava!'default.png'}"  width="50" height="50" xmlns="http://www.w3.org/2000/svg"
             preserveAspectRatio="xMidYMid slice" focusable="false" role="img" class="img-fluid rounded"/>
        <a href="/profiles/${post.authorId}" class="media-body p-3">${post.authorName}</a>
    </div>
    <#if post.filename??>
        <img class="bd-img card-img-top img-fluid" src="/img/${post.filename}" width="100%"/>
    </#if>
    <div class="card-body" id="postBody${post.id}">
        <h4>${post.tag}</h4>
        <p class="card-text">${post.text?truncate_w(100, '...')}</p>
        <div class="d-flex justify-content-between align-items-center">
            <form method="get" action="/posts/edit/${sec.curId}">
                <div class="btn-group">
                    <a href="/post/${post.id}" type="button" class="btn btn-sm btn-outline-secondary">Читать</a>
                    <#if post.getAuthorId() = sec.curId>
                        <input type="hidden" name="post" value="${post.id}"/>
                        <button type="submit" class="btn btn-sm btn-outline-secondary">
                            Редактировать
                        </button>
                    </#if>
                </div>
            </form>
            <small class="text-muted">${post.getTimeString()}</small>
        </div>
    </div>
</div>
</#macro>