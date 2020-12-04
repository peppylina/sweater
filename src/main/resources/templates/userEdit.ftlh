<#import "parts/common.ftlh" as c>

<@c.page>

<div>
    <a href="/users">Отмена</a>
</div>

User editor

<form name="editForm" action="/users" method="post" class="form-group">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="id" value="${user.id}"/>

    <input class="form-control ${(usernameError??)?string('is-invalid','')}"
           type="text" name="username" value="${user.username!''}"/>
        <#if usernameError??>
            <div class="invalid-feedback">
                ${usernameError}
            </div>
        </#if>

    <input class="form-control ${(passwordError??)?string('is-invalid','')}"
        type="password" name="password" placeholder="new password"/>
        <#if passwordError??>
            <div class="invalid-feedback">
                ${passwordError}
            </div>
        </#if>

    <input class="form-control ${(emailError??)?string('is-invalid','')}"
           type="text" name="email" value="${user.email!''}"/>
        <#if emailError??>
            <div class="invalid-feedback">
                ${emailError}
            </div>
        </#if>

   <!--списко ролей -->
    <#list roles as role>
        <div>
            <label><input type="checkbox" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}/>${role}</label>
        </div>
    </#list>


    <button type="submit">Save</button>
</form>

<!-- button for delete user -->
<form name="deleteForm" action="/users/delete" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="id" value="${user.id}"/>
    <button type="submit">Delete user</button>
</form>

</@c.page>