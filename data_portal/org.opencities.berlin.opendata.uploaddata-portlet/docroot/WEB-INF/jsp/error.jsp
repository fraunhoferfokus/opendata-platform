<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<table>
	<tr>
		<a class="anchor" href="${homeUrl}">Home</a>
	</tr>
</table>
<table>
	<tr>
		<td><c:out value="${exceptionMsg}" /></td>
	</tr>
</table>
