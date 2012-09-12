<%@ include file="init.jsp"%>

<portlet:actionURL var="linkedDataActionUrl">
	<portlet:param name="ocAction" value="linkedDataAction" />
</portlet:actionURL>
<liferay-ui:error key="virtuosoError" message="oc_virtuoso-error" />
<%
	String resource_languagePrompt = LanguageUtil.get(
			request.getLocale(), "resource_language.prompt");

	String resource_descriptionPrompt = LanguageUtil.get(
			request.getLocale(), "resource_description.prompt");
%>

<div class="oc-portlet-background oc">
	<h1 id="headline">
		<liferay-ui:message key="oc_create-linkeddata" />
	</h1>

	<form:form name="linkedDataForm" action="${linkedDataActionUrl}"
		enctype="multipart/form-data" commandName="linkedData" method="post">


		<fieldset>
			<legend>
				<liferay-ui:message key="oc_resource-information" />
			</legend>

			<label for="resource_path"><liferay-ui:message
					key="resource_path" /><span class="req">&lowast;</span></label> <input
				type="file" name="file" />
			<liferay-ui:icon-help message="resource_path.description" />
			<form:errors cssClass="error" path="validFile" />
			<br /> <label for="language"><liferay-ui:message
					key="language" /><span class="req">&lowast;</span></label>
			<form:input id="language" path="language"
				placeholder='<%= resource_languagePrompt %>' />
			<liferay-ui:icon-help message="resource_language.description" />
			<form:errors cssClass="error" path="language" />
			<br /> <label for="description"><liferay-ui:message
					key="description" /></label>
			<form:textarea id="description" path="description"
				placeholder='<%= resource_descriptionPrompt %>' />
			<liferay-ui:icon-help message="resource_description.description" />
			<form:errors cssClass="error" path="description" />
			<br />
		</fieldset>

		<input type="submit" value="<liferay-ui:message key="submit"/>" />
		<div id="legend">
			<span class="req">&lowast;</span>
			<liferay-ui:message key="mark.required-field" />
		</div>
	</form:form>

</div>