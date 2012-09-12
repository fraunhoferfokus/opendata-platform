<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:choose>
<c:when test="${not empty result.results}">
	<h3>
		<liferay-ui:message key="oc-datasets_search-results" />&nbsp;<liferay-ui:message key="oc-datasets_search-results-for" /> ${parsedSearchQuery}
	</h3>
<c:forEach items="${result.results}" var="package"
	varStatus="packageStatus">
	<div class="dataset-widget">
		<div class="dataset-leftcolumn">
			<liferay-portlet:actionURL var="latePkg">
				<liferay-portlet:param name="action" value="packageDetailsAction"></liferay-portlet:param>
<%-- 				<liferay-portlet:param name="packageId" value='${package.id}'></liferay-portlet:param> --%>
				<liferay-portlet:param name="packageId" value='${package.name}'></liferay-portlet:param>
			</liferay-portlet:actionURL>
			<div class="dataset-titleAndNotes">
				<h3>
					<a href='${latePkg}'>${package.title}</a>
				</h3>
				<p>${package.notes}</p>
			</div>
			<div class="dataset-categoriesAndAuthor">
				<liferay-ui:message key="groups" />:
				<c:forEach items='${package.groups}' var="groups" varStatus="status">
					<c:out value='${portletSessionScope.groupsData[groups].title}' />
					<c:if test="${not status.last}">,</c:if>
				</c:forEach>
				<br> <liferay-ui:message key="oc-datasets_metadata-author"/>: ${package.author}
			</div>
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
					<c:set var="date_modified" value="${package.metadata_modified}" />
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
</c:when>
<c:otherwise>
	<h3>
		<liferay-ui:message key="oc-datasets_search-no-packages-found" />
	</h3>
</c:otherwise>
</c:choose>