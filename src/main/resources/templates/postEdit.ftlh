<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>

<@c.page>

<#if status??>
    <h2>${status}</h2>
</#if>

    <!-- Если не указать action, то при отправке других форм, он будет переходить на другие урлы,
    а без этого параметра форма отправляет запрос на текущий урл. То есть, после фильтрации, мы не сможем добавить сообщение новое,
    потому что нажав кнопку найти, мы переходим на другой url: filter_by_tag или filter_by_text и уже оттуда отправляем форму на добавление
    на те контроллеры, а не на корневой, поэтому появляется ошибка-->
    <form class="was-validated" method="post" action="/posts/edit/${post.author.id!'-1'}?post=${post.id}" enctype="multipart/form-data">
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
            <input class="form-control" type="text" value="${post.tag}" name="tag" placeholder="Tag"/>
        </div>

        <!--если есть файл, то отображаем его-->
        <#if post.filename??>
            <img src="/img/${post.filename}" class="card-img-top p-2"/>
        </#if>

        <!--Кнопка для выбора файла-->
        <div class="form-group">
            <input value="${post.filename!''}" class="form-control-file" type="file" name="file"/>
        </div>
        <#if fileError??>
            <div class="invalid-feedback">
                ${fileError}
            </div>
        </#if>

        <!--btn submit-->
        <button type="submit" class="btn btn-primary">Save changes</button>

        <!-- это невидимое поле обезопасит форму от хакерских атак-->
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    </form>


</@c.page>