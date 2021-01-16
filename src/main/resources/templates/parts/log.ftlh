<#macro login path isRegisterForm>
<form action="${path}" method="post">
    <div class="form-group">
        <label for="inputUsername">User name: </label>
        <input type="text" name="username" id="inputUsername" value="<#if user??>${user.username!''}</#if>"
               class="form-control ${(usernameError??)?string('is-invalid','')}" placeholder="Name"/>
        <#if usernameError??>
        <div class="invalid-feedback">
            ${usernameError}
        </div>
        </#if>
    </div>
    <div class="form-group">
        <label for="inputPassword">Password: </label>
        <input type="password" class="form-control ${(passwordError??)?string('is-invalid','')}" name="password" id="inputPassword" placeholder="Password"/>
        <#if passwordError??>
        <div class="invalid-feedback">
            ${passwordError}
        </div>
    </#if>
    </div>
    <!-- это невидимое поле обезопасит форму от хакерских атак-->
    <input type="hidden" name="_csrf" value="${_csrf.token}"/>

    <!--если форма регистрации, то sign Up кнопка, если просто войти, то sign in-->
    <#if isRegisterForm>
        <!-- confirm password -->
        <div class="form-group">
            <label for="inputPassword2">Password: </label>
            <input type="password" class="form-control ${(password2Error??)?string('is-invalid','')}"
                   name="password2" id="inputPassword2" placeholder="Retype password"/>
            <#if password2Error??>
            <div class="invalid-feedback">
                ${password2Error}
            </div>
            </#if>
            <div class="g-recaptcha mt-3" data-sitekey="6LdZDOoZAAAAAFnwI9pgwTdEvzzdtMrWAkUtPSnu"></div>
            <#if captchaError??>
                <div class="alert alert-danger" role="alert" id="captcha_alert">
                    ${captchaError}
                </div>
            </#if>
        </div>

        <!--form for email-->
        <div class="form-group">
            <label for="inputEmail">Email: </label>
            <input type="email" class="form-control ${(emailError??)?string('is-invalid','')}"
                   name="email" id="inputEmail" placeholder="example@example.com" value="<#if user??>${user.email!''}</#if>"/>
            <#if emailError??>
            <div class="invalid-feedback">
                ${emailError}
            </div>
            </#if>
        </div>

        <button type="submit" class="btn btn-primary">Sign up!</button>
        <#else>
        <button type="submit" class="btn btn-primary">Sign in!</button>
    </#if>
</form>
<!--если это  НЕ форма регистрации, тогда показываем кнопку зарегаться-->
<#if !isRegisterForm>
    <a href="/registration">Зарегистрироваться</a>
</#if>

</#macro>





<#macro logout path>

<!-- button for logout-->
<form action="${path}" method="post" class="form-inline my-2 my-lg-0">
    <!-- это невидимое поле обезопасит форму от хакерских атак-->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Log out</button>
</form>

</#macro>