<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:if test="${isUserDataOwner or isUserDataSteward}">
	<h3>
		<liferay-ui:message key="oc-datasets_maintain-datasets-heading" />
	</h3>
	<c:if test="${isUserDataSteward}">
		<p>
			<liferay-ui:message key="oc-datasets_intro-data-steward" />
		</p>
	</c:if>
	<c:if test="${isUserDataOwner}">
		<p>
			<liferay-ui:message key="oc-datasets_intro-data-owner" />
		</p>
		<liferay-portlet:renderURL portletName="defhgfokusodpportalmanagedatasetsportlet_WAR_defhgfokusodpportalmanagedatasetsportlet" plid="${manageDatasetsPlId}" var="createDatasetAction">
			<liferay-theme:param name="ocAction" value="createMetaDataRender"></liferay-theme:param>
		</liferay-portlet:renderURL>

		<form method="post" action="${createDatasetAction}"
			style="display: inline;">
			<input class="submitButton" type="submit"
				value="<liferay-ui:message key="oc-datasets_add-new-dataset" />" />
		</form>
		<liferay-portlet:actionURL var="listMyDatasets">
				<liferay-portlet:param name="action" value="packageSearchAction" />
				<liferay-portlet:param name="searchQuery" value="maintainer=${usersFullName}"/>
		</liferay-portlet:actionURL>
		<form method="post" action="${listMyDatasets}" style="display: inline;">
			<input class="submitButton" type="submit"
				value="<liferay-ui:message key="oc-datasets_list-my-datasets" />" />
		</form>
	</c:if>
	<hr/>
</c:if>

<h3>
	<liferay-ui:message key="oc-datasets_latest-datasets" />
</h3>

<c:forEach items="${latestPackagesData.results}"
	var="package" varStatus="packageStatus">
	<div class="dataset-widget">
		<div class="dataset-leftcolumn">
			<liferay-portlet:actionURL var="latePkg">
				<liferay-portlet:param name="action" value="packageDetailsAction"></liferay-portlet:param>
<%-- 				<liferay-portlet:param name="packageId" value='${package.value.id}'></liferay-portlet:param> --%>
				<liferay-portlet:param name="packageId" value="${package.name}"></liferay-portlet:param>
			</liferay-portlet:actionURL>
			<div class="dataset-titleAndNotes">
				<h3>
					<a href='${latePkg}'>${package.title}</a>
				</h3>
				<p>${package.notes}</p>
			</div>
			<span class="dataset-categoriesAndAuthor">
				<liferay-ui:message key="groups" />:
				<c:forEach items='${package.groups}' var="groups"
					varStatus="status">
					<c:out value='${portletSessionScope.groupsData[groups].title}' />
					<c:if test="${not status.last}">,</c:if>
				</c:forEach>
				<br> <liferay-ui:message key="oc-datasets_metadata-author"/>: ${package.author}
			</span>
		</div>
		<div class="dataset-rightcolumn">
			<div class=dataset-resources>
				<c:forEach var="resource_url" items="${package.res_url}"
					varStatus="status">
					<a href="${resource_url}"><img class="test"
						src="${pageContext.request.contextPath}/images/format/${fn:toLowerCase(package.res_format[status.index])}.png" alt="${fn:toLowerCase(package.res_format[status.index])}" title="${fn:toLowerCase(package.res_format[status.index])}"/></a>
				</c:forEach>
				<c:choose>
				<c:when test="${arePackagesOpen[packageStatus.index]}">
					<h4><liferay-ui:message key="oc-datasets_open" /></h4>
				</c:when>
				<c:otherwise>
					<h4><liferay-ui:message key="oc-datasets_not-open" /></h4>
				</c:otherwise>
				</c:choose>
			</div>
			<hr class="lineDivider">
			<div class=dataset-commentsandratings>
				<div class="dataset-commentsandratings-box">
					<liferay-ui:message key="oc-datasets_metadata-modified" />:<br />
					<c:set var="date_modified"
						value="${package.metadata_modified}" />
					<p class="dataset-commentsandratings-value">${fn:substring(date_modified,0,10)}</p>
				</div>
				<div class="dataset-commentsandratings-box">
					<img id="commentsImg"
						src="<%=request.getContextPath()%>/images/commentsgrey.png"
						class="smallCommentsAndRatings" /><br>
					<p class="dataset-commentsandratings-value">${commentsNumber[packageStatus.index]}</p>
				</div>
				<div class="dataset-commentsandratings-box">
					<img id="ratingsImg"
						src="<%=request.getContextPath()%>/images/ratingsgrey.png"
						class="smallCommentsAndRatings" /><br>
					<p class="dataset-commentsandratings-value">${ratingsNumber[packageStatus.index]}</p>
				</div>
			</div>
		</div>
	</div>
</c:forEach>