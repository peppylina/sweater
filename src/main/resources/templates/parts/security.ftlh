<!--в хтлш можно писать скрипты и присваивать переменные.
Так, тут мы при вызове данного шаблона искать сессию и определять имя пользователя и является ли он админом-->
<#assign
    know = Session.SPRING_SECURITY_CONTEXT??>

<#if know>
    <#assign
        user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
        name = user.getUsername()
        isAdmin = user.isAdmin()
        curId = user.getId()
        authorised = true>
    <#else>
        <#assign
            name = "unknown"
            isAdmin = false
            curId = -1
            authorised = false>
</#if>