<%@ include file="init.jsp"%>

<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="art">
	<liferay-theme:param name="groups" value="rec"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="statistics">
	<liferay-theme:param name="groups" value="stats"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="demographics">
	<liferay-theme:param name="groups" value="demographics"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="business">
	<liferay-theme:param name="groups" value="business"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="construction">
	<liferay-theme:param name="groups" value="housing"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="education">
	<liferay-theme:param name="groups" value="edu"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="budget">
	<liferay-theme:param name="groups" value="budget"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="crime">
	<liferay-theme:param name="groups" value="safety"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="elections">
	<liferay-theme:param name="groups" value="elections"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="emergency">
	<liferay-theme:param name="groups" value="emergency"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="law">
	<liferay-theme:param name="groups" value="law"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="transport">
	<liferay-theme:param name="groups" value="transport"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="environment">
	<liferay-theme:param name="groups" value="environment"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="political">
	<liferay-theme:param name="groups" value="politics"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="others">
	<liferay-theme:param name="groups" value="misc"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="health">
	<liferay-theme:param name="groups" value="health"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="tourism">
	<liferay-theme:param name="groups" value="tourism"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="energy">
	<liferay-theme:param name="groups" value="energy"></liferay-theme:param>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL
	portletName="ocdatasetsportlet_WAR_ocdatasetsportlet" plid="${plId}"
	var="employment">
	<liferay-theme:param name="groups" value="employment"></liferay-theme:param>
</liferay-portlet:renderURL>

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