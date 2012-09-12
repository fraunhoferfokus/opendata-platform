<%@ include file="init.jsp"%>

<portlet:actionURL var="metaDataActionUrl">
	<portlet:param name="ocAction" value="metaDataAction" />
	<c:choose>
		<c:when test="${create}">

			<portlet:param name="action" value="createMetaData" />
		</c:when>
		<c:otherwise>
			<portlet:param name="action" value="editMetaData" />
		</c:otherwise>
	</c:choose>
</portlet:actionURL>

<portlet:actionURL var="cancelActionUrl">
	<portlet:param name="ocAction" value="cancelAction" />
</portlet:actionURL>

<script type="text/javascript">
	jQuery(document).ready(
					function() {
						jQuery(
								"#date_released, #temporal_coverage_from, #temporal_coverage_to")
								.datepicker({
									dateFormat : 'yy-mm-dd',
								});
					});
</script>

<%
	String titlePrompt = LanguageUtil.get(request.getLocale(),
			"title.prompt");

	String namePrompt = LanguageUtil.get(request.getLocale(),
			"name.prompt");

	String authorPrompt = LanguageUtil.get(request.getLocale(),
			"author.prompt");

	String author_emailPrompt = LanguageUtil.get(request.getLocale(),
			"author_email.prompt");

	String websitePrompt = LanguageUtil.get(request.getLocale(),
			"url.prompt");

	String notesPrompt = LanguageUtil.get(request.getLocale(),
			"notes.prompt");

	String date_releasedPrompt = LanguageUtil.get(request.getLocale(),
			"date_released.prompt");

	String tagsPrompt = LanguageUtil.get(request.getLocale(),
			"tags.prompt");

	String versionPrompt = LanguageUtil.get(request.getLocale(),
			"version.prompt");

	String temporal_coverage_fromPrompt = LanguageUtil.get(
			request.getLocale(), "temporal_coverage_from.prompt");

	String temporal_coverage_toPrompt = LanguageUtil.get(
			request.getLocale(), "temporal_coverage_from.prompt");

	String temporal_granularityPrompt = LanguageUtil.get(
			request.getLocale(), "temporal_granularity.prompt");

	String geographical_coveragePrompt = LanguageUtil.get(
			request.getLocale(), "geographical_coverage.prompt");

	String geographical_granularityPrompt = LanguageUtil.get(
			request.getLocale(), "geographical_granularity.prompt");

	String othersPrompt = LanguageUtil.get(request.getLocale(),
			"others.prompt");

	String resource_languagePrompt = LanguageUtil.get(
			request.getLocale(), "resource_language.prompt");

	String resource_descriptionPrompt = LanguageUtil.get(
			request.getLocale(), "resource_description.prompt");

	String description = LanguageUtil.get(request.getLocale(),
			"oc_description");

	String format = LanguageUtil.get(request.getLocale(), "oc_format");

	String language = LanguageUtil.get(request.getLocale(),
			"oc_language");
%>

<script type="text/javascript">
	function generateName() {

		var name = document.getElementById("title").value;

		name = name.replace(/ /g, "-").toLowerCase();

		document.getElementById("name").value = name;

	}
</script>

<div class="oc-portlet-background oc">
	<h1 id="headline">${heading}</h1>

	<form:form name="startpageSearch" action="${metaDataActionUrl}"
		commandName="metaData" method="post">
		<fieldset>
			<legend>
				<liferay-ui:message key="oc_common-information" />
			</legend>
			<label for="title"><liferay-ui:message key="title" /><span
				class="req">&lowast;</span></label>
			<form:input id="title" path="title" onblur="generateName();"
				placeholder='<%=titlePrompt%>' />
			<liferay-ui:icon-help message="title.description" />
			<form:errors cssClass="error" path="title" />
			<br /> <label for="name"><liferay-ui:message key="name" /><span
				class="req">&lowast;</span></label>
			<form:input id="name" path="name" readonly="true"
				placeholder='<%=namePrompt%>' />
			<liferay-ui:icon-help message="name.description" />
			<form:errors cssClass="error" path="name" />
			<br /> <label for="author"><liferay-ui:message key="author" /><span
				class="req">&lowast;</span></label>
			<form:input id="author" path="author" placeholder='<%=authorPrompt%>' />
			<liferay-ui:icon-help message="author.description" />
			<form:errors cssClass="error" path="author" />
			<br /> <label for="author_email"><liferay-ui:message
					key="author_email" /><span class="req">&lowast;</span></label>
			<form:input id="author_email" path="author_email"
				placeholder='<%=author_emailPrompt%>' />
			<liferay-ui:icon-help message="author_email.description" />
			<form:errors cssClass="error" path="author_email" />
			<br /> <label for="website"><liferay-ui:message
					key="website" /></label>
			<form:input id="website" path="url" placeholder='<%=websitePrompt%>' />
			<liferay-ui:icon-help message="url.description" />
			<form:errors cssClass="error" path="url" />
			<br /> <label for="notes"><liferay-ui:message key="notes" /></label>
			<form:textarea id="notes" path="notes" placeholder='<%=notesPrompt%>' />
			<liferay-ui:icon-help message="notes.description" />
			<form:errors cssClass="error" path="notes" />
			<br /> <label for="license"><liferay-ui:message
					key="license_id" /><span class="req">&lowast;</span></label>
			<form:select id="license" path="license_id" items="${licences}"
				itemLabel="label" itemValue="value" />
			<liferay-ui:icon-help message="license_id.description" />
			<form:errors cssClass="error" path="license_id" />
			<br /> <label for="date_released"><liferay-ui:message
					key="date_released" /><span class="req">&lowast;</span></label>
			<form:input id="date_released" path="date_released"
				placeholder='<%=date_releasedPrompt%>' readonly="true" />
			<liferay-ui:icon-help message="date_released.description" />
			<form:errors cssClass="error" path="date_released" />
			<br /> <label for="groups"><liferay-ui:message key="groups" /><span
				class="req">&lowast;</span></label>
			<form:select id="groups" path="groups" items="${categories}"
				itemLabel="label" itemValue="value" multiple="true" size="7" />
			<liferay-ui:icon-help message="groups.description" />
			<form:errors cssClass="error" path="groups" />
			<br /> <label for="tags"><liferay-ui:message key="tags" /></label>
			<form:input id="tags" path="tags" placeholder='<%=tagsPrompt%>' />
			<liferay-ui:icon-help message="tags.description" />
			<form:errors cssClass="error" path="tags" />
			<br /> <label for="version"><liferay-ui:message
					key="version" /></label>
			<form:input id="version" path="version"
				placeholder='<%=versionPrompt%>' />
			<liferay-ui:icon-help message="version.description" />
			<form:errors cssClass="error" path="version" />
			<br />
		</fieldset>

		<fieldset>
			<legend>
				<liferay-ui:message key="oc_temproal-geographics" />
			</legend>

			<label for="temporal_coverage_from"><liferay-ui:message
					key="temporal_coverage_from" /></label>
			<form:input id="temporal_coverage_from" path="temporal_coverage_from"
				placeholder='<%=temporal_coverage_fromPrompt%>' />
			<liferay-ui:icon-help message="temporal_coverage_from.description" />
			<form:errors cssClass="error" path="temporal_coverage_from" />
			<br /> <label for="temporal_coverage_to"><liferay-ui:message
					key="temporal_coverage_to" /></label>
			<form:input id="temporal_coverage_to" path="temporal_coverage_to"
				placeholder='<%=temporal_coverage_toPrompt%>' />
			<liferay-ui:icon-help message="temporal_coverage_to.description" />
			<form:errors cssClass="error" path="temporal_coverage_to" />
			<br /> <label for="temporal_granularity"><liferay-ui:message
					key="temporal_granularity" /></label>
			<form:input id="temporal_granularity" path="temporal_granularity"
				placeholder='<%=temporal_granularityPrompt%>' />
			<liferay-ui:icon-help message="temporal_granularity.description" />
			<form:errors cssClass="error" path="temporal_granularity" />
			<br /> <label for="geographical_coverage"><liferay-ui:message
					key="geographical_coverage" /></label>
			<form:input id="geographical_coverage" path="geographical_coverage"
				placeholder='<%=geographical_coveragePrompt%>' />
			<liferay-ui:icon-help message="geographical_coverage.description" />
			<form:errors cssClass="error" path="geographical_coverage" />
			<br /> <label for="geographical_granularity"><liferay-ui:message
					key="geographical_granularity" /></label>
			<form:input id="geographical_granularity"
				path="geographical_granularity"
				placeholder='<%=geographical_granularityPrompt%>' />
			<liferay-ui:icon-help message="geographical_granularity.description" />
			<form:errors cssClass="error" path="geographical_granularity" />
			<br />
		</fieldset>

		<fieldset>
			<legend>
				<liferay-ui:message key="oc_misc-information" />
			</legend>

			<label for="others"><liferay-ui:message key="others" /></label>
			<form:textarea id="others" path="others"
				placeholder='<%=othersPrompt%>' />
			<liferay-ui:icon-help message="others.description" />
			<form:errors cssClass="error" path="others" />
			<br />
		</fieldset>

		<fieldset>
			<legend>
				<liferay-ui:message key="oc_resource-information" />
			</legend>
			<c:forEach items="${metaData.resources}" varStatus="resource">
				<label for="<c:out value="${status.expression}"/>"><liferay-ui:message
						key="resource" /> <c:out value="${resource.index + 1}" /></label>
				<spring:bind path="metaData.resources[${resource.index}].url">
					<input class="res-wide" type="text"
						name="<c:out value="${status.expression}"/>"
						id="<c:out value="${status.expression}"/>"
						value="<c:out value="${status.value}"/>" placeholder="URL" />
				</spring:bind>
				<spring:bind
					path="metaData.resources[${resource.index}].description">
					<input class="res-wide" type="text"
						name="<c:out value="${status.expression}"/>"
						id="<c:out value="${status.expression}"/>"
						value="<c:out value="${status.value}"/>"
						placeholder="<%= description %>" />
				</spring:bind>
				<spring:bind path="metaData.resources[${resource.index}].language">
					<input class="res-small" type="text"
						name="<c:out value="${status.expression}"/>"
						id="<c:out value="${status.expression}"/>"
						value="<c:out value="${status.value}"/>"
						placeholder="<%= language %>" />
				</spring:bind>
				<spring:bind path="metaData.resources[${resource.index}].format">
					<input class="res-small" type="text"
						name="<c:out value="${status.expression}"/>"
						id="<c:out value="${status.expression}"/>"
						value="<c:out value="${status.value}"/>"
						placeholder="<%= format %>" />
				</spring:bind>
				<liferay-ui:icon-help message="oc_resource-format-help" />
				<br />
			</c:forEach>

		</fieldset>

		<!-- 
		Hiddenfields to store data
		 -->
		<div style="display: none;">
			<form:input path="ckanId" />
			<form:input path="maintainer" />
			<form:input path="maintainer_email" />
			<form:input path="metadata_created" />
			<form:input path="metadata_modified" />
			<form:input path="date_updated" />
			<form:input path="date_released" />
		</div>
		<!-- 
        End Hiddenfields to store data
         -->

		<input type="submit" value="<liferay-ui:message key="submit"/>" />
	</form:form>

	<form:form name="cancelForm" action="${cancelActionUrl}"
		commandName="metaData" method="post">
		<form:hidden id="title" path="title" readonly="true" />
		<input type="submit" style="margin-left: 1em;"
			value="<liferay-ui:message key="cancel"/>" />
		<div id="legend">
			<span class="req">&lowast;</span>
			<liferay-ui:message key="mark.required-field" />
		</div>
	</form:form>

	<div style="clear: both;"></div>

</div>