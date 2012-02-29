<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="<%=request.getLocale()%>"/>
<fmt:setBundle basename="content.Language-ext"/>

<table>
	<tr>
		<a class="anchor" href="${homeUrl}">Home</a>
	</tr>
</table>
<table>
  <tr>
  	<td>
  		File upload was successful.
  	</td>
  </tr>
</table>