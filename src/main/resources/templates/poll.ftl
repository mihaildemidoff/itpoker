Task: <b>${taskName}</b>

<#if finished>
<#if hasDecision>✅ Decision: <b>${decision}</b><#else>⛔ No decision</#if>

</#if>
<b>Votes (${votes?size}):</b>
<#list votes as vote>
<a href="tg://user?id=${vote.userId}">${vote.firstName!} ${vote.lastName!}</a> <#if finished> - <b>${vote.value}</b></#if>
</#list>
