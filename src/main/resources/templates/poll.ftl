Task: <b>${taskName}</b>


<#if finished>
Consensus - <#if consensus>✅<#else>⛔</#if>
</#if>
<b>Votes:</b>
<#list votes as vote>
<a href="tg://user?id=${vote.userId}">${vote.firstName!} ${vote.lastName!}</a> <#if finished> - <b>${vote.value}</b></#if>
</#list>
