<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>

<@c.page>

<!-- надпись, которую нам может отправить сервер, показывваем, только если сервер нам что-то прислал-->
<div>
    <#if response??>
    <h2>${response}</h2>
    </#if>
</div>

<a class="btn btn-primary m-2" data-toggle="collapse" href="#collapseForm"
   role="button" aria-expanded="false" aria-controls="collapseForm">Add post</a>
<div class="collapse m-2" id="collapseForm">
    <!-- Если не указать action, то при отправке других форм, он будет переходить на другие урлы,
    а без этого параметра форма отправляет запрос на текущий урл. То есть, после фильтрации, мы не сможем добавить сообщение новое,
    потому что нажав кнопку найти, мы переходим на другой url: filter_by_tag или filter_by_text и уже оттуда отправляем форму на добавление
    на те контроллеры, а не на корневой, поэтому появляется ошибка-->
    <form class="was-validated" method="post" action="/news" enctype="multipart/form-data">
        <!--текст сообщения -->
        <div class="mb-3">
            <label for="textTextArea">Text</label>
            <textarea id="textTextArea" class="form-control ${(textError??)?string('is-invalid','')}"
                      name="text"  placeholder="Enter post" required="required"><#if post??>${post.text!''}</#if></textarea>
            <#if textError??>
            <div class="invalid-feedback">
                ${textError}
            </div>
            </#if>
        </div>

        <!-- Тэг сообщения-->
        <div class="mb-3">
            <input class="form-control" type="text" name="tag" placeholder="Tag"/>
        </div>

        <!--Кнопка для выбора файла-->
        <div class="form-group">
            <input class="form-control-file" type="file" name="file"/>
        </div>
        <#if fileError??>
            <div class="invalid-feedback">
                ${fileError}
            </div>
        </#if>

        <!--btn submit-->
        <button type="submit" class="btn btn-primary">Add</button>

        <!-- это невидимое поле обезопасит форму от хакерских атак-->
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>
</div>

<!-- фильтр сообщений по TAG -->
<div>
    <form method="get" action="/news" class="form-inline m-2">
        <div class="form-group mb-2">
            <input class="form-control" value="${tagFilter!}" type="text" name="tagFilter"/>
        </div>

        <button type="submit" class="btn btn-primary mb-2">Search by Tag</button>

        <!-- это невидимое поле обезопасит форму от хакерских атак, нужен только для post запросов-->
        <!--<input type="hidden" name="_csrf" value="${_csrf.token}"/>-->
    </form>
</div>

<!--фильтр по TEXT -->
<div>
    <form method="get" action="/news" class="form-inline m-2">
        <div class="form-group mb-2">
            <input  class="form-control" type="text" name="textFilter" value="${textFilter!}"/>
        </div>

        <button type="submit" class="btn btn-primary mb-2">Search by Text</button>
        <!-- это невидимое поле обезопасит форму от хакерских атак-->
        <!--<input type="hidden" name="_csrf" value="${_csrf.token}"/>-->
    </form>
</div>

<!-- Кнопка для прогрузки предыдущих постов, если они есть -->
<#assign num = startIndex?number>
<#if (num > 0) && (!tagFilter?has_content) && (!textFilter?has_content)>
<form method="get" action="/news/less">
    <input type="hidden" name="startIndex" value="${startIndex}"/>
    <button class="btn btn-outline-warning btn-lg btn-block my-3" type="submit">Загрузить предыдущие посты</button>
</form>
</#if>
<!-- выводим на экран сообщения -->

<div> Список сообщений </div>
<div class="card-rows" id="posts_list">
    <#list posts as post>
        <div class="card border-success m-5" data-id="${post.id}">
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
            </div>
        </div>
    <#else>
    No result.
    </#list>
</div>

<!-- Кнопка для прогрузки еще постов -->
<#if (!tagFilter?has_content) && (!textFilter?has_content)>
<form method="get" action="/news/more">
    <input type="hidden" name="startIndex" value="${startIndex}"/>
    <button class="btn btn-outline-warning btn-lg btn-block my-3" type="submit">Загрузить еще посты</button>
</form>
</#if>


</@c.page>