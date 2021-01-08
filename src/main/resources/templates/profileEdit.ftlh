<#import "parts/common.ftlh" as c>
<#import "parts/log.ftlh" as l>

<@c.page>

<#if isChanged??>
    <#assign user = Session.SPRING_SECURITY_CONTEXT.authentication.principal>
</#if>

<!--action не указываем, так как отправляем на тот же адрес post-запрос-->
<form method="post" class="was-validated" enctype="multipart/form-data">
    <!-- AVA -->
    <div class="row">
        <div class="col-md-auto my-3">
            <img width="150" src="${hostUrl}/img/avas/<#if user.ava??>${user.ava}<#else>default.png</#if>"/>
        </div>
        <div class="col-md-auto my-3">
            <p style="font-size:20px;color:orange">Изменить заставку:</p>
            <input class="form-control-file" type="file" name="file"/>
        </div>
        <#if fileError??>
            <div class="invalid-feedback">
                ${fileError}
            </div>
        </#if>
        <!-- Кнопка удалить аватарку -->
        <div class="col-md my-3">
            <input type="submit" class="btn btn-danger" formaction="/profiles/deleteAva" value="Удалить изображение"/>
        </div>
    </div>
    <div class="form-group">
        <!-- USERNAME -->
        <label for="inputUsername">User name: </label>
        <input type="text" name="username" id="inputUsername" class="form-control ${(usernameError??)?string('is-invalid', '')}"
               placeholder="Name" value="<#if user??>${user.username!''}</#if>" required/>
        <#if usernameError??>
            <div class="invalid-feedback">
                ${usernameError}
            </div>
        </#if>
    </div>
    <!-- PASSWORD -->
    <div class="form-group">
        <label for="inputPassword">Password: </label>
        <!--<input type="hidden" name="password" value="${user.password!'1'}">-->
        <input type="password" class="form-control ${(passwordError??)?string('is-invalid', '')}"
               name="password" id="inputPassword" placeholder="New password"/>
        <#if passwordError??>
            <div class="invalid-feedback">
                ${passwordError}
            </div>
        </#if>
    </div>
    <!-- PASSWORD2 -->
    <div class="form-group">
        <label for="inputPassword2">Confirm password: </label>
        <!--<input type="hidden" name="password2" value="${user.password!'1'}">-->
        <input type="password" class="form-control ${(password2Error??)?string('is-invalid', '')}"
               name="password2" id="inputPassword2" placeholder="New password"/>
        <#if password2Error??>
            <div class="invalid-feedback">
                ${password2Error}
            </div>
        </#if>
    </div>
    <!-- EMAIL -->
    <div class="form-group">
        <label for="inputEmail">Email: </label>
        <input type="email" class="form-control ${(emailError??)?string('is-invalid', '')}"
               name="email" id="inputEmail" placeholder="Email" value="<#if user??>${user.email!''}</#if>"/>
        <#if emailError??>
            <div class="invalid-feedback">
                ${emailError}
            </div>
        </#if>
    </div>
    <!-- это невидимое поле обезопасит форму от хакерских атак-->
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>
    <!-- USER_ID -->
    <input type="hidden" name="id" value="<#if user??>${user.id!'1'}</#if>">

    <button type="submit" class="btn btn-primary">Save</button>
</form>

</@c.page>