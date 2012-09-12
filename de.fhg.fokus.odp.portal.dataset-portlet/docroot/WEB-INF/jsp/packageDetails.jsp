<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />
<script type="text/javascript">
	var siteroot = "${pageContext.servletContext.contextPath}";
	var packageRating = ${packageRating};
</script>
<c:set var="result" value="${result}" />
		<c:if test="${isUserDataOwner}">
			<div style="float: right; margin-top: 8px; padding-right: 11px; padding-bottom:15px">
				<portlet:actionURL var="deleteDatasetURL">
					<portlet:param name="action" value="packageDeleteAction" />
					<portlet:param name="packageId" value="${result.name}" />
				</portlet:actionURL>
				<form method="post" action="${deleteDatasetURL}" style="display: inline;">
	        		<input class="submitButton" type="submit" value="<liferay-ui:message key="oc-datasets_delete-dataset" />" onclick="return confirm('Do you really want to delete this dataset?')" />
				</form>
			</div>
		</c:if>
<div class="packageDetailsWidget">
	<div class="packageDetails-leftColumn">
		<c:if test="${isUserDataOwner}">
			<div style="float: right; margin-top: 8px; padding-right: 5px;">
				<liferay-portlet:renderURL
					portletName="defhgfokusodpportalmanagedatasetsportlet_WAR_defhgfokusodpportalmanagedatasetsportlet"
					plid="${manageDatasetsPlId}" var="editDatasetAction">
					<liferay-theme:param name="ocAction" value="editMetaDataRender"></liferay-theme:param>
					<liferay-theme:param name="packageId" value='${result.id}'></liferay-theme:param>
				</liferay-portlet:renderURL>

				<form method="post" action="${editDatasetAction}" style="display: inline;">
					<input class="submitButton" type="submit"
						value="<liferay-ui:message key="oc-datasets_edit-dataset" />" />
				</form><br/>
			</div>
		</c:if>
		<div class="packageDetails-titleNotesAndAuthor">
			<h3>${result.title}</h3>
			<p>${result.notes}</p>
			<p>
				<liferay-ui:message key="oc-datasets_metadata-author"/>: ${result.author}<br /> 
				<b><a href="${result.url}">${result.url}</a></b>
			</p>
		</div>
		<div class="packageDetails-resourceWidget">
			<c:if test="${isUserDataSteward}">
				<div style="float: right; padding-bottom: 7px; padding-right: 5px;">
					<liferay-portlet:renderURL
						portletName="defhgfokusodpportalmanagedatasetsportlet_WAR_defhgfokusodpportalmanagedatasetsportlet"
						plid="${manageDatasetsPlId}" var="createLinkedMetaDataRender">
						<liferay-theme:param name="ocAction"
							value="createLinkedMetaDataRender"></liferay-theme:param>
						<liferay-theme:param name="packageId" value='${result.id}'></liferay-theme:param>
					</liferay-portlet:renderURL>

<!-- TODO check if virtuoso settings are set -->
					<form method="post" action="${createLinkedMetaDataRender}"
						style="display: inline;">
						<input class="submitButton" type="submit"
							value="<liferay-ui:message key="oc-datasets_add-linked-data-resource" />" disabled="disabled"/>
					</form>
				</div>
			</c:if>
			<div style="clear:both"></div>
			<c:forEach items='${result.resources}' var="resource">
				<c:if test="${not (resource.resource_type == 'documentation') }">
					<div class="packageDetails-resource">
						<span class="packageDetails-resource-description">${resource.description}</span>
						<span class="packageDetails-resource-language">| ${empty resource.language ? 'None' : resource.language } </span>
						<span class="packageDetails-resource-format"><a href="${resource.url}"><img class="test" src="${pageContext.request.contextPath}/images/format/${fn:toLowerCase(resource.format)}.png" alt="${fn:toLowerCase(resource.format)}" title="${fn:toLowerCase(resource.format)}"/></a></span>
					</div>
				</c:if>
			</c:forEach>
				<%-- documentation --%>
			<h4>
				<liferay-ui:message key="oc-datasets_documentation"></liferay-ui:message>:<br/>
			</h4>
			<c:forEach items='${result.resources}' var="resource">
				<c:if test="${ resource.resource_type == 'documentation' }">
					<div class="packageDetails-resource">
						<span class="packageDetails-resource-description">${resource.description}</span>
						<span class="packageDetails-resource-language">| ${empty resource.language ? 'None' : resource.language } </span> 
						<span class="packageDetails-resource-format"><a href="${resource.url}"><img class="test" src="${pageContext.request.contextPath}/images/format/${fn:toLowerCase(resource.format)}.png" alt="${fn:toLowerCase(resource.format)}"/></a></span>
					</div>
				</c:if>
			</c:forEach>
		</div>
		<div class="packageDetails-categoriesAndTags">
			<b><liferay-ui:message key="groups" />:</b>
			<c:forEach items='${result.groups}' var="groups" varStatus="status">
				<c:out value='${portletSessionScope.groupsData[groups].title}' />
				<c:if test="${not status.last}">,</c:if>
			</c:forEach>
			<br /> <b><liferay-ui:message key="oc-datasets_metadata-tags"/>:</b>
			<c:forEach items='${result.tags}' var="tags">
				<c:out value='${tags}' />
			</c:forEach>
		</div>
	</div>
	<div class="packageDetails-rightColumn">
		<div class="packageDetails-openBox">
			<c:choose>
				<c:when test="${isPackageOpen}">
					<h3><liferay-ui:message key="oc-datasets_open" /></h3>
				</c:when>
				<c:otherwise>
					<h3><liferay-ui:message key="oc-datasets_not-open" /></h3>
				</c:otherwise>
			</c:choose>
			<liferay-ui:message key="oc-datasets_metadata-license" />:<br />
			<div class="packageDetails-additionalInformation-value">${result.license}</div>
		</div>
		<hr class="lineDivider">
		<div class="packageDetails-additionalInformation">
			<liferay-ui:message key="oc-datasets_metadata-modified" />:<br />
			<c:set var="date_modified" value="${result.metadata_modified}" />
			<div class="packageDetails-additionalInformation-value">${fn:substring(date_modified,0,10)}</div>
			<liferay-ui:message key="oc-datasets_metadata-created" />:<br />
			<c:set var="date_released" value="${result.metadata_created}" />
			<div class="packageDetails-additionalInformation-value">${fn:substring(date_released,0,10)}</div>
			<liferay-ui:message key="oc-datasets_metadata-area"/>:<br />
			<div class="packageDetails-additionalInformation-value">${result.extras.geographical_coverage}</div>
			<liferay-ui:message key="oc-datasets_metadata-period"/>:<br />
			<c:set var="date_updated"
				value="${result.extras.temporal_coverage_from}" />
			<c:set var="date_released"
				value="${result.extras.temporal_coverage_to}" />
			<div class="packageDetails-additionalInformation-value">${fn:substring(date_updated,0,10)}
				&dash; ${fn:substring(date_released,0,10)}</div>
		</div>
		<div class="packageDetails-ratings">
			<h4>
				<liferay-ui:message key="oc-datasets_metadata-rating"/> (${fn:length(result.extras.ratings)})
			</h4>
			<div id="average-rating-box">
				<img id="average-rating-star1" class="rating-star"
					src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png" />
				<img id="average-rating-star2" class="rating-star"
					src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png" />
				<img id="average-rating-star3" class="rating-star"
					src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png" />
				<img id="average-rating-star4" class="rating-star"
					src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png" />
				<img id="average-rating-star5" class="rating-star"
					src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png" />
			</div>
		</div>
	</div>
</div>
<div>
	<a href="#" onclick="showCommentsInputBox()"> <img id="ratingsImg"
		src="${pageContext.servletContext.contextPath}/images/interaction/Comment.png"
		class="packageDetails-imgSocialInteraction" /> <liferay-ui:message key="oc-datasets_comment-dataset"/>
	</a> <span style="margin-left: 8%;"> <a href="#"
		onclick="showRatingsInputBox()"> <img id="ratingsImg"
			src="${pageContext.servletContext.contextPath}/images/interaction/Rating.png"
			class="packageDetails-imgSocialInteraction" /> <liferay-ui:message key="oc-datasets_rate-dataset"/>
	</a>
	</span> <span style="margin-left: 8%;"> <a
		href="mailto:${result.author_email}"> <img id="ratingsImg"
			src="${pageContext.servletContext.contextPath}/images/interaction/Mail.png"
			class="packageDetails-imgSocialInteraction" /> <liferay-ui:message key="oc-datasets_mail-dataset"/>
	</a>
	</span> <span style="margin-left: 8%;">
	<a href="https://twitter.com/share?url=${twitterURL}/-/data/view/${result.name}&text=Look%20at%20this%20dataset"
		target="_blank"> <img id="ratingsImg"
			src="${pageContext.servletContext.contextPath}/images/interaction/Twitter.png"
			class="packageDetails-imgSocialInteraction" /> <liferay-ui:message key="oc-datasets_twitter-dataset"/>
	</a>
	</span>
</div>
<div id="comments-input-box">
	<c:choose>
		<c:when test="${isUserLoggedIn}">
			<p><liferay-ui:message key="oc-datasets_add-comment"/>:</p>
			<form method="post"
				action="<portlet:actionURL><liferay-portlet:param name="action" value="packageDetailsAction"></liferay-portlet:param>
				<liferay-portlet:param name="socialInteraction" value="comment"></liferay-portlet:param></portlet:actionURL>">
				<textarea name="userComment" style="width: 100%; height: 80px;"></textarea>
				<%-- 				<input type="hidden" name="packageId" value="${result.id}" /> <input --%>
				<input type="hidden" name="packageId" value="${result.name}" /> <input
					type="submit" value="Submit" />
			</form>
		</c:when>
		<c:when test="${not isUserLoggedIn}">
			<p><liferay-ui:message key="oc-datasets_not-logged-in-comment"/></p>
		</c:when>
	</c:choose>
</div>
<div id="ratings-input-box">
	<portlet:actionURL var="ratingURL1">
		<portlet:param name="action" value="packageDetailsAction" />
		<portlet:param name="socialInteraction" value="rating"></portlet:param>
		<%-- 			<portlet:param name="packageId" value='${result.id}' /> --%>
		<portlet:param name="packageId" value='${result.name}' />
		<portlet:param name="userRating" value="1" />
	</portlet:actionURL>
	<portlet:actionURL var="ratingURL2">
		<portlet:param name="action" value="packageDetailsAction" />
		<portlet:param name="socialInteraction" value="rating"></portlet:param>
		<%-- 			<portlet:param name="packageId" value='${result.id}' /> --%>
		<portlet:param name="packageId" value='${result.name}' />
		<portlet:param name="userRating" value="2" />
	</portlet:actionURL>
	<portlet:actionURL var="ratingURL3">
		<portlet:param name="action" value="packageDetailsAction" />
		<portlet:param name="socialInteraction" value="rating"></portlet:param>
		<%-- 			<portlet:param name="packageId" value='${result.id}' /> --%>
		<portlet:param name="packageId" value='${result.name}' />
		<portlet:param name="userRating" value="3" />
	</portlet:actionURL>
	<portlet:actionURL var="ratingURL4">
		<portlet:param name="action" value="packageDetailsAction" />
		<portlet:param name="socialInteraction" value="rating"></portlet:param>
		<%-- 			<portlet:param name="packageId" value='${result.id}' /> --%>
		<portlet:param name="packageId" value='${result.name}' />
		<portlet:param name="userRating" value="4" />
	</portlet:actionURL>
	<portlet:actionURL var="ratingURL5">
		<portlet:param name="action" value="packageDetailsAction" />
		<portlet:param name="socialInteraction" value="rating"></portlet:param>
		<%-- 			<portlet:param name="packageId" value='${result.id}' /> --%>
		<portlet:param name="packageId" value='${result.name}' />
		<portlet:param name="userRating" value="5" />
	</portlet:actionURL>

	<c:choose>
		<c:when test="${isUserLoggedIn}">
			<c:if test="${packageRatingPermission}">
				<p>
					<liferay-ui:message key="oc-datasets_add-rating"/>:<br /> <span id="rating-control">
						<a href="${ratingURL1}"><img id="rating-star1"
							class="rating-star"
							src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png"
							onmouseover="lightStars(1)" onmouseout="dimStars()" /></a><a
						href="${ratingURL2}"><img id="rating-star2"
							class="rating-star"
							src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png"
							onmouseover="lightStars(2)" onmouseout="dimStars()" /></a><a
						href="${ratingURL3}"><img id="rating-star3"
							class="rating-star"
							src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png"
							onmouseover="lightStars(3)" onmouseout="dimStars()" /></a><a
						href="${ratingURL4}"><img id="rating-star4"
							class="rating-star"
							src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png"
							onmouseover="lightStars(4)" onmouseout="dimStars()" /></a><a
						href="${ratingURL5}"><img id="rating-star5"
							class="rating-star"
							src="${pageContext.servletContext.contextPath}/images/ratingsgrey.png"
							onmouseover="lightStars(5)" onmouseout="dimStars()" /></a>
					</span>
				</p>
			</c:if>
			<c:if test="${not packageRatingPermission}">
				<p><liferay-ui:message key="oc-datasets_already-rated"/></p>
			</c:if>
		</c:when>
		<c:when test="${not isUserLoggedIn}">
			<p><liferay-ui:message key="oc-datasets_not-logged-in-rating"/></p>
		</c:when>
	</c:choose>
</div>
<div class="comments-widget">
	<h4>
		<liferay-ui:message key="oc-datasets_comments"/> (${fn:length(comments)})
	</h4>
	<c:forEach items="${comments}" var="comment" varStatus="commentStatus">
		<div class="comment-box">
			<p>${comment.comment}</p>
			${commentersNames[commentStatus.index]} <liferay-ui:message key="oc-datasets_comment-on"/> ${comment.date}
		</div>
	</c:forEach>
</div>