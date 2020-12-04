<#import "security.ftlh" as sec>
<!--спискок сообщений-->
<#list user.messages as mess>
    <div class="card m-5">
        <#if mess.filename??>
            <img src="/img/${mess.filename}" class="card-img-top p-2"/>
        </#if>

        <div class="card-body">
            <#if mess.tag??>
            <h5 class="card-title">${mess.tag}</h5>
        </#if>
        <p class="card-text">${mess.text!" "}</p>
        <a href="/profiles/${mess.authorId}" class="card-link">${mess.authorName}</a>

        <!-- button for edit -->
        <#if mess.author.id == sec.curId>
            <a href="/messages/edit/${mess.author.id}?message=${mess.id}" class="btn btn-primary btn-sm">Редактировать сообщение</a>
        </#if>
        </div>
</div>
</#list>