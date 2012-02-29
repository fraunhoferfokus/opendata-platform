<%@ include file="init.jsp"%>
<%
	int countDataets = 0;
	int countApps = 0;
%>

<div>
	<div id="bottom" class="wrapper oc-portlet-background">
		<div id="first" class="left1">
			<h3>
				<liferay-ui:message key="oc_popular-datasets" />
			</h3>
			<table class="box-table">
				<c:forEach items="${datasets}" var="dataset" varStatus="row">
					<tr height="15px">
						<td><a href="${dataset.url}">${dataset.title}</a></td>
						<td class="rating-width">
							<%
								int starsOff = 5;
									List<BoxEntry> boxEntries = (List<BoxEntry>) request
											.getAttribute("datasets");
									BoxEntry boxEntry = boxEntries.get(countDataets);
									countDataets++;
									double rating = boxEntry.getRating();
									int starsOn = (int) Math.floor(rating);
									int halfStar = (starsOn < rating) ? 1 : 0;
									starsOff = starsOff - starsOn - halfStar;
									if (starsOff > 5)
										starsOff = 5;
									for (int i = 1; i <= starsOn; i++) {
							%><img src="<%=request.getContextPath()%>/img/star_on.png"
							class="rating-star" /> <%
 	}
 		if (halfStar == 1) {
 %><img src="<%=request.getContextPath()%>/img/star_half.png"
							class="rating-star" /> <%
 	}
 		for (int i = 1; i <= starsOff; i++) {
 %><img src="<%=request.getContextPath()%>/img/star_off.png"
							class="rating-star" /> <%
 	}
 %>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>

		<div id="middle" class="left2">
			<h3>
				<liferay-ui:message key="oc_popular-apps"></liferay-ui:message>
			</h3>
			<table class="box-table">
				<tr height="15px">
					<td>FixMyCity</td>
					<td class="rating-width">
						<%
							int halfStar = 0;
							int starsOff = 0;
							for (int i = 1; i <= 5; i++) {
						%><img src="<%=request.getContextPath()%>/img/star_on.png"
						class="rating-star" /> <%
 	}
 	if (halfStar == 1) {
 %><img src="<%=request.getContextPath()%>/img/star_half.png"
						class="rating-star" /> <%
 	}
 	for (int i = 1; i <= starsOff; i++) {
 %><img src="<%=request.getContextPath()%>/img/star_off.png"
						class="rating-star" /> <%
 	}
 %>
					</td>
				</tr>
				<tr height="15px">
					<td>DataMap</td>
					<td class="rating-width">
						<%
							halfStar = 0;
							starsOff = 0;
							for (int i = 1; i <= 5; i++) {
						%><img src="<%=request.getContextPath()%>/img/star_on.png"
						class="rating-star" /> <%
 	}
 	if (halfStar == 1) {
 %><img src="<%=request.getContextPath()%>/img/star_half.png"
						class="rating-star" /> <%
 	}
 	for (int i = 1; i <= starsOff; i++) {
 %><img src="<%=request.getContextPath()%>/img/star_off.png"
						class="rating-star" /> <%
 	}
 %>
					</td>
				</tr>
				<tr height="15px">
					<td>BudgetCalc</td>
					<td class="rating-width">
						<%
							halfStar = 1;
							starsOff = 0;
							for (int i = 1; i <= 4; i++) {
						%><img src="<%=request.getContextPath()%>/img/star_on.png"
						class="rating-star" /> <%
 	}
 	if (halfStar == 1) {
 %><img src="<%=request.getContextPath()%>/img/star_half.png"
						class="rating-star" /> <%
 	}
 	for (int i = 1; i <= starsOff; i++) {
 %><img src="<%=request.getContextPath()%>/img/star_off.png"
						class="rating-star" /> <%
 	}
 %>
					</td>
				</tr>
			</table>
		</div>

		<div class="left3">
			<h3>
				<liferay-ui:message key="oc_popular-ideas" />
			</h3>
			<table class="box-table">
				<c:forEach items="${discussions}" var="dataset" varStatus="row">
					<tr height="15px">
						<td><a href="${dataset.url}">${dataset.title}</a></td>
						<td>${dataset.votes} <liferay-ui:message key="oc_votes" /></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
<div style="clear: both"></div>