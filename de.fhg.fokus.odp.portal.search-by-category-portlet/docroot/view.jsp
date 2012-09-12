<%@ include file="init.jsp"%>

<liferay-portlet:actionURL var="art">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="rec" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="statistics">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="stats" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="demographics">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="demographics" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="business">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="business" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="construction">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="housing" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="education">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="edu" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="budget">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="budget" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="crime">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="safety" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="elections">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="elections" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="emergency">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="emergency" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="law">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="law" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="transport">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="transport" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="environment">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="environment" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="political">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="politics" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="others">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="misc" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="health">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="health" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="tourism">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="tourism" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="energy">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="energy" />
</liferay-portlet:actionURL>
<liferay-portlet:actionURL var="employment">
	<liferay-portlet:param name="action" value="packageSearchAction" />
	<liferay-portlet:param name="searchQuery" value="employment" />
</liferay-portlet:actionURL>

<script type="text/javascript">
	function toggle(obj) {

		var first = document.getElementById("first");
		var second = document.getElementById("second");

		if (first.style.display != 'none') {
			first.style.display = 'none';
			second.style.display = '';
		} else {
			second.style.display = 'none';
			first.style.display = '';
		}
	}
</script>

<div class="ccontent oc-portlet-background">
	<div id="first">
		<div class="cwrapper">
			<div class="cleft1">
				<div class="image">
					<img src="<%=request.getContextPath()%>/images/OC_Icon_01_Arts.png"
						width="50" height="50">
				</div>
				<div>

					<b><a href="<%=art%>"><liferay-ui:message
								key="oc_category-art-recreation" /></a></b>
					<p>
						<liferay-ui:message key="oc_category-art-recreation-description" />
					</p>
				</div>
			</div>
			<div class="cleft2">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_02_Business.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=business%>"><liferay-ui:message
								key="oc_category-business-enterprise" /></a> </b>
					<p>
						<liferay-ui:message
							key="oc_category-business-enterprise-description" />
					</p>
				</div>
			</div>
			<div class="cleft3">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_03_Budget.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=budget%>"><liferay-ui:message
								key="oc_category-city-budget" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-city-budget-description" />
					</p>
				</div>
			</div>
		</div>
		<div class="cwrapper">
			<div class="cleft1">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_04_Statistics.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=statistics%>"><liferay-ui:message
								key="oc_category-city-portal-statistic" /></a> </b>
					<p>
						<liferay-ui:message
							key="oc_category-city-portal-statistic-description" />
					</p>
				</div>
			</div>
			<div class="cleft2">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_05_Construction.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=construction%>"><liferay-ui:message
								key="oc_category-construction-housing" /></a> </b>
					<p>
						<liferay-ui:message
							key="oc_category-construction-housing-description" />
					</p>
				</div>
			</div>
			<div class="cleft3">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_06_Crime.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=crime%>"><liferay-ui:message
								key="oc_category-crime-safety" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-crime-safety-description" />
					</p>
				</div>
			</div>
		</div>
		<div class="cwrapper">
			<div class="cleft1">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_07_Demographics-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=demographics%>"><liferay-ui:message
								key="oc_category-demographics" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-demographics-description" />
					</p>
				</div>
			</div>
			<div class="cleft2">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_08_Education-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=education%>"><liferay-ui:message
								key="oc_category-education" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-education-description" />
					</p>
				</div>
			</div>
			<div class="cleft3">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_09_Elections-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=elections%>"><liferay-ui:message
								key="oc_category-elections" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-elections-description" />
					</p>
				</div>
			</div>
		</div>
	</div>
	<div id="second" style="display: none;">
		<div class="cwrapper">
			<div class="cleft1">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_10_Emergency-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=emergency%>"><liferay-ui:message
								key="oc_category-emergency-services" /></a> </b>
					<p>
						<liferay-ui:message
							key="oc_category-emergency-services-description" />
					</p>
				</div>
			</div>
			<div class="cleft2">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_12_Environment-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=environment%>"><liferay-ui:message
								key="oc_category-environment-geography" /></a> </b>
					<p>
						<liferay-ui:message
							key="oc_category-environment-geography-description" />
					</p>
				</div>
			</div>
			<div class="cleft3">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_13_Health-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=health%>"><liferay-ui:message
								key="oc_category-health-disability" /></a> </b>
					<p>
						<liferay-ui:message
							key="oc_category-health-disability-description" />
					</p>
				</div>
			</div>
		</div>
		<div class="cwrapper">
			<div class="cleft1">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_15_Law-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=law%>"><liferay-ui:message
								key="oc_category-law" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-law-description" />
					</p>
				</div>
			</div>
			<div class="cleft2">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_16_Politics-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=political%>"><liferay-ui:message
								key="oc_category-political" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-political-description" />
					</p>
				</div>
			</div>
			<div class="cleft3">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_17_Tourism-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=tourism%>"><liferay-ui:message
								key="oc_category-tourism" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-tourism-description" />
					</p>
				</div>
			</div>
		</div>
		<div class="cwrapper">
			<div class="cleft1">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_18_Mobility-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=transport%>"><liferay-ui:message
								key="oc_category-urban-transport" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-urban-transport-description" />
					</p>
				</div>
			</div>
			<div class="cleft2">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_14_Labor-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=employment%>"><liferay-ui:message
								key="oc_category-employment" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-employment-description" />
					</p>
				</div>
			</div>
			<div class="cleft3">
				<div class="image">
					<img
						src="<%=request.getContextPath()%>/images/OC_Icon_11_Energy-01.png"
						width="50" height="50">
				</div>
				<div>
					<b><a href="<%=energy%>"><liferay-ui:message
								key="oc_category-energy-utilities" /></a> </b>
					<p>
						<liferay-ui:message key="oc_category-energy-utilities-description" />
					</p>
				</div>
			</div>
		</div>
	</div>
	<div style="text-align: right; margin-right: 10px;">
		<a href="javascript: toggle()" id="toggle"><liferay-ui:message
				key="oc_switch-categories" /></a>
	</div>
</div>