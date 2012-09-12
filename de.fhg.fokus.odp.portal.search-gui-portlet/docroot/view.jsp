<%@ page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<portlet:defineObjects />

<portlet:actionURL var="packageDetailsActionUrl"
	name="packageDetailsAction" />

<portlet:actionURL var="packageSearchActionUrl"
	name="packageSearchAction" />

<div id="searchwrapper">
	<form:form name="searchForm" id="searchForm"
		action="${packageSearchActionUrl}" commandName="searchForm">
		<form:input path="query" type="text" name="search"
			id="generalSearchInput"
			placeholder="<%=LanguageUtil.get(pageContext, \"search\")%>"
			accesskey="s" />

		<input id="searchButton" type="submit"
			value='<%=LanguageUtil.get(pageContext, "search")%>' />
		<input id="extendSearchButton" type="button"
			value='<%=LanguageUtil.get(pageContext, "extended-search")%>' 
      <% // next line is workaround for working toggling in IE9 %>
      onclick="jQuery181('#advancedSearchFields').toggle(500)"
      />

		<div id="advancedSearchFields">
			<form:label path="title" for="title">
				<liferay-ui:message key="title" />
			</form:label>
			<form:input path="title" type="text" name="" />
			<form:errors path="title" />
			<br />

			<form:label path="maintainer">
				<liferay-ui:message key="oc-datasets_metadata-maintainer" />
			</form:label>
			<form:input path="maintainer" type="text" name="" />
			<form:errors path="maintainer" />
			<br />

			<form:label path="author">
				<liferay-ui:message key="author" />
			</form:label>
			<form:input path="author" type="text" name="" />
			<form:errors path="author" />
			<br />

			<form:label path="description">
				<liferay-ui:message key="description" />
			</form:label>
			<form:input path="description" type="text" name="" />
			<form:errors path="description" />
			<br />

			<form:label path="tags">
				<liferay-ui:message key="tags" />
			</form:label>
			<form:input path="tags" type="text" name="" />
			<form:errors path="tags" />
			<br />

			<form:label path="group">
				<liferay-ui:message key="category" />
			</form:label>
			<form:select id="group" path="group" items="${groups}"
				itemLabel="translatedName" itemValue="key" />
			<form:errors cssClass="error" path="group" />

			<form:label path="geographical_coverage">
				<liferay-ui:message key="geographical_coverage" />
			</form:label>
			<form:input path="geographical_coverage" type="text" name="" />
			<form:errors path="geographical_coverage" />
			<br />

			<form:label path="geographical_granularity">
				<liferay-ui:message key="geographical_granularity" />
			</form:label>
			<form:input path="geographical_granularity" type="text" name="" />
			<form:errors path="geographical_granularity" />
			<br />

			<form:label path="temporal_granularity">
				<liferay-ui:message key="temporal_granularity" />
			</form:label>
			<form:input path="temporal_granularity" type="text" name="" />
			<form:errors path="temporal_granularity" />
			<br />

			<form:label path="temporal_coverage_from">
				<liferay-ui:message key="temporal_coverage_from" />
			</form:label>
			<form:input id="temporal_coverage_from" path="temporal_coverage_from"
				type="text" name="" placeholder="YYYY-MM-DD" />
			<form:errors path="temporal_coverage_from" />
			<br />

			<form:label path="temporal_coverage_to">
				<liferay-ui:message key="temporal_coverage_to" />
			</form:label>
			<form:input id="temporal_coverage_to" path="temporal_coverage_to"
				type="text" name="" placeholder="YYYY-MM-DD" />
			<form:errors path="temporal_coverage_to" />
			<br /> <label for="reset" hidden="true">&emsp;</label> <input
				id="reset" type="button" onclick="clearForm(this.form);"
				value='<%=LanguageUtil.get(pageContext, "reset")%>'>
		</div>
	</form:form>
</div>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.8.1.min.js"></script> 
<script type="text/javascript">
var jQuery181 = jQuery.noConflict(true);
/* used by main.js*/
jQuery181.portletImageFolder = "<%=request.getContextPath()%>/img/";
</script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/main.js"></script>
