<%@ include file="init.jsp"%>

<%
	String version = PropsUtil.get("platform.about.version");
	String name = PropsUtil.get("platform.about.name");
	String releaseDate = PropsUtil.get("platform.about.releasedate");
	String city = PropsUtil.get("platform.about.city");
%>

<div class="oc-portlet-background" style="padding: 15px">
	<h1><%=name%></h1>
	
	<table>
		<tr>
			<td><liferay-ui:message key="oc_version" />:
			<td>
			<td><span><%=version%></span> <br />
			<td>
		</tr>
	   <tr>
            <td><liferay-ui:message key="oc_city" />:
            <td>
            <td><span><%=city%></span> <br />
            <td>
        </tr>
		<tr>
			<td><liferay-ui:message key="oc_releasedate" />:
			<td>
			<td><span><%=releaseDate%></span>
		</tr>
	</table>
</div>