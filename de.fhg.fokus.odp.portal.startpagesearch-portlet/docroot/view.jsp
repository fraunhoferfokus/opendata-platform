<%@ include file="init.jsp"%>

<portlet:actionURL var="searchActionUrl">
	<portlet:param name="dcAction" value="search" />
</portlet:actionURL>

<form:form name="startpageSearch" action="${searchActionUrl}"
	commandName="searchQuery" method="post">
	<span class="searchLabel"><liferay-ui:message key="search" /></span>
	<span class="searchInput"><form:input path="query"
			class="searchqueryInput" /></span>
	<input style="min-width: 150px;" type="submit"
		value="<liferay-ui:message key="submit"/>" />
</form:form>