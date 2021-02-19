<#macro pager url page>

    <!-- gt - больше,  lt- меньше-->
    <#if page.getTotalPages() gt 7>
        <#assign
            totalPages = page.getTotalPages()
            curPageNum = page.getNumber() + 1
            head = (curPageNum > 4)?then([1, -1], [1, 2, 3])
            tail = (curPageNum < totalPages - 3)?then([-1, totalPages], [totalPages - 2, totalPages - 1, totalPages])
            bodyBefore = (curPageNum > 4 && curPageNum < totalPages - 1)?then([curPageNum - 2, curPageNum - 1], [])
            bodyAfter = (curPageNum > 2 && curPageNum < totalPages - 3)?then([curPageNum + 1, curPageNum + 2], [])

            body = head + bodyBefore + (curPageNum > 3 && curPageNum < totalPages - 2)?then([curPageNum], []) + bodyAfter + tail/>
    <#else>
        <#assign body = 1..page.getTotalPages()/>
    </#if>

    <div class="mt-2">
        <ul class="pagination">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Страницы</a>
            </li>
            <#list body as p>
                <#if p == (page.getNumber() + 1)>
                    <li class="page-item active">
                        <a class="page-link" href="#" tabindex="${p}" aria-disabled="true">${p}</a>
                    </li>
                <#elseif p == (-1)>
                    <li class="page-item disabled">
                        <a class="page-link" href="#" tabindex="-1" aria-disabled="true">...</a>
                    </li>
                <#else>
                    <li class="page-item">
                        <a class="page-link" href="${url}?page=${p - 1}&size=${page.getSize()}" tabindex="${p}" aria-disabled="true">${p}</a>
                    </li>
                </#if>
            </#list>
        </ul>
    </div>

    <div class="mt-2">
        <ul class="pagination">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1" aria-disabled="true">Элементов на странице:</a>
            </li>
            <#list [1, 5, 10, 25, 50] as s>
                <#if s == page.getSize()>
                    <li class="page-item active">
                        <a class="page-link" href="#" tabindex="${s}" aria-disabled="true">${s}</a>
                    </li>
                <#else>
                    <li class="page-item">
                        <a class="page-link" href="${url}?page=${page.getNumber()}&size=${s}" tabindex="${s}" aria-disabled="true">${s}</a>
                    </li>
                </#if>
            </#list>
    </div>
</#macro>