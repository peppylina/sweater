<#import "log.ftlh" as logout>
<#import "security.ftlh" as sec>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="/">Sweater</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link" href="/">Home </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/news">News</a>
            </li>
            <!--кнопочка для админов-->
            <#if sec.isAdmin>
                <li class="nav-item">
                    <a class="nav-link" href="/users">Users manager</a>
                </li>
            </#if>
            <!--если авторизованы - появляется кнопочка редактирования профиля-->
            <#if sec.authorised>
                <li class="nav-item">
                    <a class="nav-link" href="/profiles/editProfile">Settings</a>
                </li>
            </#if>
            <!-- кнопка мой профиль -->
            <#if sec.authorised>
                <li class="nav-item">
                    <a class="nav-link" href="/profiles/${sec.curId}">My profile</a>
                </li>
            </#if>
        </ul>
        <!-- username text:-->
        <div class="navbar-text mr-5">${sec.name}</div>

        <!-- кнопочка выйти, если мы зарегистрировались-->
        <#if sec.authorised>
            <@logout.logout "/logout" />
        <#else>
            <!--если не авторизованы, то кнопка войти-->
            <a href="/login">Sign In</a>
        </#if>

    </div>
</nav>