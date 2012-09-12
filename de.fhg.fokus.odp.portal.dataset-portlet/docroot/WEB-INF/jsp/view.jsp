<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="oc-portlet-background">
	<div
		style="padding: 0 0px 0px 10px; background: transparent; float: left; width:32%">
		<h3>
			<liferay-ui:message key="oc-datasets_metadata-categories" />
		</h3>
		<c:choose>
			<c:when test="${ ! empty portletSessionScope.groupsContainer}">
				<liferay-ui:search-iterator searchContainer="${portletSessionScope.groupsContainer}" />
			</c:when>
			<c:otherwise>
				<liferay-ui:message key="oc-datasets_no-categories" />
			</c:otherwise>
		</c:choose>
		<%-- show popularTagsData in a cloud style --%>
		<div id="tagcloud">
			<c:forEach items='${portletSessionScope.popularTagsData}' var="tags">
				<liferay-portlet:actionURL var="popularTag">
					<liferay-portlet:param name="action" value="packageSearchAction"></liferay-portlet:param>
					<liferay-portlet:param name="searchQuery" value="tags=${tags.tag_name}"></liferay-portlet:param>
				</liferay-portlet:actionURL>
				<a href="${popularTag}" class="tagCloudAnchor" title="${tags.count}">${tags.tag_name}</a>
			</c:forEach>
		</div>
	</div>
	<div id="contentPane">
		<c:choose>
			<c:when test="${action == 'defaultView'}">
				<jsp:include page="latest.jsp" />
			</c:when>
			<c:when test="${action == 'packageDetailsAction'}">
				<jsp:include page="packageDetails.jsp" />
			</c:when>
			<c:when test="${action == 'packageSearchAction'}">
				<jsp:include page="packageSearch.jsp" />
			</c:when>
		</c:choose>
	</div>
</div>