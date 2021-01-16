<#import "parts/common.ftlh" as c>
<#import "parts/security.ftlh" as sec>

<@c.page>

<div class="blog-post">
    <h2 class="blog-post-title">${post.tag}</h2>
    <p class="blog-post-meta">${post.getTimeString()} by <a href="/profiles/${post.authorId}">${post.authorName}</a></p>

    <#if post.filename??>
        <div class="jumbotron p-4 p-md-5 text-white rounded bg-dark">
            <img src="/img/${post.filename}" width="100%">
        </div>
    </#if>

    <p>${post.text}</p>

</div>

</@c.page>