<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui" %>
<%@ page contentType="text/html" isELIgnored="false"%>

<ol>
<li>Download template:</li>
&nbsp;&nbsp;<a class="anchor" href='<portlet:resourceURL id="template.xlsx"/>'>Download</a>
<li>Fill in the required information.</li>
<li>Upload File:</li>
</ol>
<form name="uploadForm" method="post" action='<portlet:actionURL name="uploadAction"></portlet:actionURL>' enctype="multipart/form-data">
		<b>File:</b><font style="color: #C11B17;"></font>
		<input type="file" name="xlsxFile" /><br/>
		<input type="submit" value="Upload XLSX" onclick="return loadSubmit()"/>
</form>
<p style="visibility:hidden;" id="progress"/><img id="progress_image" style="padding-left:5px;padding-top:5px;" src="<%=request.getContextPath()%>/images/ajax-loader.gif" alt=""> In progress...<p> 