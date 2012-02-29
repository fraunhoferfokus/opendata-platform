<%--
/**
* Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
--%>

<%@page import="com.liferay.portal.kernel.util.PropsUtil"%>
<%@page import="com.liferay.portal.service.CompanyLocalServiceUtil"%>
<%@page import="com.liferay.portal.service.RoleLocalServiceUtil"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchEntry"%>
<%@ page import="com.liferay.portal.kernel.util.CalendarFactoryUtil"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil"%>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="com.liferay.portlet.PortletPreferencesFactoryUtil"%>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>

<%@ page import="java.net.URI"%>
<%@ page import="java.text.DateFormat"%>

<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>

<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.RenderResponse"%>
<%@ page import="javax.portlet.RenderRequest"%>

<%@ page import="javax.ws.rs.core.UriBuilder"%>

<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>


<portlet:defineObjects />
<liferay-theme:defineObjects />


<aui:layout>

	<%
		SearchContainer<String> containerGroups = (SearchContainer<String>) request
					.getAttribute("groupsContainer");
			/*SearchContainer<String> containerTags = (SearchContainer<String>) request
					.getAttribute("tagsContainer");*/
			SearchContainer<String> containerPackages = (SearchContainer<String>) request
					.getAttribute("packagesContainer");
			SearchContainer<String> containerPackageResources = (SearchContainer<String>) request
					.getAttribute("packageResourcesContainer");

			
			HashMap<String, Object> groupsData = (HashMap) portletSession
					.getAttribute("groupsData");
			

			String searchMode = (String) portletSession.getAttribute("searchMode");
			
			String searchparams_query = (String) request
					.getAttribute("searchparams");

			Map<String, String> searchparams = new HashMap<String, String>();

			if (searchparams_query != null) {
				for (String param : searchparams_query.split("&")) {
					String[] pair = param.split("=");
					if (pair == null || pair.length != 2)
						break;
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = URLDecoder.decode(pair[1], "UTF-8");
					searchparams.put(key, value);
				}
			}

			String searchhint = "";
			if (searchparams.size() == 0)
				searchhint = LanguageUtil.get(request.getLocale(), "oc-datasets_search-error-no-terms"); 

			// Set empty default values
			String[] other_params = { "q", "title", "notes", "author",
					"maintainer", "groups", "tags" };
			for (String param : other_params) {
				if (searchparams.get(param) == null)
					searchparams.put(param, "");
			}
			if (searchparams.get("filter_by_openness") != null)
				searchparams.put("filter_by_openness",
						"checked=\"checked\"");
			else
				searchparams.put("filter_by_openness", "");

			if (searchparams.get("filter_by_downloadable") != null)
				searchparams.put("filter_by_downloadable",
						"checked=\"checked\"");
			else
				searchparams.put("filter_by_downloadable", "");
			

			JSONObject packageDetails = (JSONObject) request
					.getAttribute("packageDetails");
			
			double packageRating = (request
			.getAttribute("packageRating") != null) ? (Double) request
					.getAttribute("packageRating") : -1;
			boolean packageRatingPermission = (request
					.getAttribute("packageRatingPermission") != null) ? (Boolean) request
							.getAttribute("packageRatingPermission") : false;
			
			int packageCommentsCount = (request
					.getAttribute("packageCommentsCount") != null) ? (Integer) request
			.getAttribute("packageCommentsCount") : -1;
			JSONArray packageComments = (request
					.getAttribute("packageComments") != null) ? (JSONArray) request
			.getAttribute("packageComments") : null;
			
			JSONArray popularTags = (portletSession
					.getAttribute("popularTagsData") != null) ? (JSONArray) portletSession
			.getAttribute("popularTagsData") : null;
			
			HashMap<String, HashMap<String, String>> latestPackages = (portletSession
					.getAttribute("latestPackagesData") != null) ? (HashMap<String, HashMap<String, String>>) portletSession
			.getAttribute("latestPackagesData") : null;
			
			// Consider the user logged if the user ID is different from the default user's ID
			boolean isUserLoggedIn = themeDisplay.getUserId() != themeDisplay.getDefaultUser().getUserId();
			
			// Determine if user can maintain datasets, i.e. is a member of the DataOwner/DataSteward roles
			long companyId = CompanyLocalServiceUtil.getCompanies().get(0).getCompanyId(); //TODO: move to config?
			
			long dataOwnerRoleId = RoleLocalServiceUtil.getRole(companyId, "DataOwner").getRoleId();
			boolean isUserDataOwner = UserLocalServiceUtil.hasRoleUser(dataOwnerRoleId, themeDisplay.getUserId());
			
			long dataStewardRoleId = RoleLocalServiceUtil.getRole(companyId, "DataSteward").getRoleId();
			boolean isUserDataSteward =  UserLocalServiceUtil.hasRoleUser(dataStewardRoleId, themeDisplay.getUserId());;
			
			System.out.println("VirtuosoUrl: "+PropsUtil.get("VirtuosoUrl"));
			if (PropsUtil.get("VirtuosoUrl") == null || PropsUtil.get("VirtuosoUrl").equals("")) {
				isUserDataSteward = false;
			}		
			
			
			int managaDatasetsPlId = Integer.parseInt(PropsUtil.get("page.managedatasets.plid"));
			
	%>

<div class="oc-portlet-background">

	<div id="searchPane">

		<div
			style="background: white; padding: 0 10px 10px 10px; border: 1px solid #c0c2c5; -webkit-border-top-left-radius: 4px; -webkit-border-bottom-left-radius: 4px; -moz-border-radius-topleft: 4px; -moz-border-radius-bottomleft: 4px; border-top-left-radius: 4px; border-bottom-left-radius: 4px; margin-right: -1px;">

			<h3>

				<a href="#" onclick="searchMode('extended')"
					id="searchModeLinkExtended" class="searchModeLink"><liferay-ui:message
						key="oc-datasets_extended-search" />
				</a> <a href="#" onclick="searchMode('simple')"
					id="searchModeLinkSimple" class="searchModeLink"><liferay-ui:message
						key="oc-datasets_simple-search" />
				</a>

				<liferay-ui:icon image="search" />
				<liferay-ui:message key="oc-datasets_package-search" />
			</h3>

			<form method="post"
				action="<portlet:actionURL name="packageSearchAction" />">

				<div id="searchFormSimple" class="searchForm">
					<table>
						<tr>
							<td><input type="text" name="searchquery" id="queryfield"
								value="<%=searchparams.get("q")%>"
								onsubmit="if (this.value=='Suchbegriff...') this.value='';"
								onfocus="if (this.value=='Suchbegriff...') this.value='';"
								style="" /></td>
							<td style="text-align: right;" width="50"><input
								type="submit"
								value="<liferay-ui:message key="oc-datasets_search" />"
								style="margin-left: 10px;" /></td>
						</tr>
					</table>

				</div>

				<div id="searchFormExtended" class="searchForm">
					<table>
						<!--tr>
					<td>Beliebiges Feld:</td>
					<td><input type="text" name="searchquery"  id="queryfield2"
						id="titlefield" value="<%=searchparams.get("q")%>" />
					</td>
				</tr-->
						<tr>
							<td><liferay-ui:message key="oc-datasets_metadata-title" />:</td>
							<td><input type="text" name="searchquery_title"
								id="titlefield" value="<%=searchparams.get("title")%>" />
							</td>
						</tr>
						<tr>
							<td><liferay-ui:message
									key="oc-datasets_metadata-description" />:</td>
							<td><input type="text" name="searchquery_notes"
								id="notesfield" value="<%=searchparams.get("notes")%>" />
							</td>
						</tr>
						<tr>
							<td><liferay-ui:message key="oc-datasets_metadata-author" />:</td>
							<td><input type="text" name="searchquery_author"
								id="authorfield" value="<%=searchparams.get("author")%>" />
							</td>
						</tr>
						<tr>
						<td><liferay-ui:message key="oc-datasets_metadata-maintainer" />:</td>
						<td><input type="text" name="searchquery_maintainer"
							id="maintainerfield" value="<%=searchparams.get("maintainer")%>" />
						</td>
					</tr>
						<tr>
							<td><liferay-ui:message key="oc-datasets_metadata-category" />:</td>
							<td><input type="text" name="searchquery_groups"
								id="groupsfield" value="<%=searchparams.get("groups")%>" />
							</td>
						</tr>
						<tr>
							<td><liferay-ui:message key="oc-datasets_metadata-tags" />:</td>
							<td><input type="text" name="searchquery_tags"
								id="tagsfield" value="<%=searchparams.get("tags")%>" />
							</td>
						</tr>

					</table>

					<div style="text-align: right; float: right; margin: 15px 0 0 0;">
						<input type="submit"
							value="<liferay-ui:message key="oc-datasets_search" />" />
					</div>

					<div style="margin: 10px 0 0 0;">
						<input type="checkbox" name="searchquery_openness"
							id="opennessfield" <%=searchparams.get("filter_by_openness")%> />
						<label for="opennessfield"><liferay-ui:message
								key="oc-datasets_search-open-license-only" />
						</label><br /> <input type="checkbox" name="searchquery_downloadable"
							id="downloadablefield"
							<%=searchparams.get("filter_by_downloadable")%> /> <label
							for="downloadablefield"><liferay-ui:message
								key="oc-datasets_search-linked-data-only" />
						</label>
					</div>



				</div>
			</form>

		</div>

		<div style="padding: 0 10px 10px 10px; background: transparent;">


			<h3>				
				<liferay-ui:message key="oc-datasets_metadata-categories" />
			</h3>

			<%
			if (containerGroups != null) {
		%>

			<liferay-ui:search-iterator searchContainer="<%=containerGroups %>" />

			<%
			} else {
		%>
			<p>
				<liferay-ui:message key="oc-datasets_no-categories" />
			</p>
			<%
			}
			/*
		--%>

			<h3>
				<liferay-ui:icon image="folder_open" />
				<liferay-ui:message key="oc-datasets_metadata-tags" />
			</h3>

			<%
			if (containerTags != null) {
		%>

			<liferay-ui:search-iterator searchContainer="<%=containerTags %>" />

			<%
			} else {
		%>
			<p>
				<liferay-ui:message key="oc-datasets_no-tags" />
			</p>
			<%
			}
			*/
		%>

		</div>

	</div>


	<div id="contentPane">

		<%
			if (packageDetails != null) {
		
		
		
	if (isUserDataOwner) { %>
		
		<div style="float: right; margin-top: 8px; "><liferay-ui:message key="oc-datasets_data-owner" />: 
		
		
		<liferay-portlet:renderURL
		    portletName="ocmanagedatasetsportlet_WAR_ocmanagedatasetsportlet" plid="<%= managaDatasetsPlId %>"
		    var="editDatasetAction">
		    <liferay-theme:param name="ocAction" value="editMetaDataRender"></liferay-theme:param>
		    <liferay-theme:param name="pId" value='<%=(String)packageDetails.get("id")%>'></liferay-theme:param>
		</liferay-portlet:renderURL>
		
		<form method="post"
                action="<%=editDatasetAction%>" style="display: inline;">
                
        <input type="submit" value="<liferay-ui:message key="oc-datasets_edit-dataset" />" />
        
        </form>
        
        <portlet:actionURL var="deleteDatasetURL">
			<portlet:param name="action" value="packageDetailsAction" />
			<portlet:param name="packageId" value='<%=(String)packageDetails.get("id") %>' />
			<% if (packageDetails.get("state").equals("active"))  {%>
			<portlet:param name="packageState" value="deleted" />
			<% } else { %>
			<portlet:param name="packageState" value="active" />
			<% } %>
		</portlet:actionURL>
		
		<form method="post"
                action="<%=deleteDatasetURL%>" style="display: inline;">
        
		<% if (packageDetails.get("state").equals("active"))  {%>
		<input type="submit" value="<liferay-ui:message key="oc-datasets_hide-dataset" />" onclick="return confirm('Do you really want to hide this dataset?')" />
		<% } else { %>
		<input type="submit" value="<liferay-ui:message key="oc-datasets_publish-dataset" />" onclick="return confirm('Do you really want to publish this dataset?')" />
		<% } %>
		
		</form>
		
		</div>
						
		<% } 
	%>

		<h3>
			<liferay-ui:icon image="pages" />
			<liferay-ui:message key="oc-datasets_details-for-package-heading" />
			&bdquo;<%=packageDetails.get("title")%>&ldquo;
		</h3>

		<p>
			<liferay-ui:message key="oc-datasets_metadata-source" />: 
			<a href="<%=packageDetails.get("url")%>" target="_blank"><%=packageDetails.get("url")%></a>
		</p>

		<p><%=packageDetails.get("notes")%></p>

		<% if (isUserDataSteward) { %>
		
		<div style="float: right; padding-bottom: 7px; "><liferay-ui:message key="oc-datasets_data-steward" />: 
		
		<liferay-portlet:renderURL
		    portletName="ocmanagedatasetsportlet_WAR_ocmanagedatasetsportlet" plid="<%= managaDatasetsPlId %>"
		    var="createLinkedMetaDataRender">
		    <liferay-theme:param name="ocAction" value="createLinkedMetaDataRender"></liferay-theme:param>
		    <liferay-theme:param name="pId" value='<%=(String)packageDetails.get("id")%>'></liferay-theme:param>
		</liferay-portlet:renderURL>
		
		<form method="post"
                action="<%=createLinkedMetaDataRender%>" style="display: inline;">
                
        <input type="submit" value="<liferay-ui:message key="oc-datasets_add-linked-data-resource" />" />
        
        </form>
		
		</div>
						
		<% } %>

		<h4>
			<liferay-ui:message
				key="oc-datasets_details-downloads-and-resources-heading" />
		</h4>

		<%
			if (containerPackageResources != null) {
		%>
		<div style="margin: 10px 0; clear: right;">
			<liferay-ui:search-iterator
				searchContainer="<%=containerPackageResources %>" />
		</div>
		<%
			} else {
		%>
		<p>
			<liferay-ui:message
				key="oc-datasets_details-downloads-and-resources-not-available" />
		</p>
		<%
			}
		%>

		<h4>
			<liferay-ui:message
				key="oc-datasets_details-additional-information-heading" />
		</h4>

		<table>
			<tr>
				<td width="100"><liferay-ui:message
						key="oc-datasets_metadata-author" />:</td>
				<td>
					<%
						if (packageDetails.containsKey("author_email") && packageDetails.get("author_email") != null) {
					%> <a href="mailto:<%=packageDetails.get("author_email")%>"><%=packageDetails.get("author")%></a>
					<%
						} else if (packageDetails.containsKey("author") && packageDetails.get("author") != null) {
					%> <%=packageDetails.get("author")%> <%
 	} else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<% /* %><tr>
				<td><liferay-ui:message key="oc-datasets_metadata-maintainer" />:</td>
				<td>
					<%
						if (packageDetails.containsKey("maintainer_email") && packageDetails.get("maintainer_email") != null) {
					%> <a href="mailto:<%=packageDetails.get("maintainer_email")%>"><%=packageDetails.get("maintainer")%></a>
					<%
						} else if (packageDetails.containsKey("maintainer") && packageDetails.get("maintainer") != null) {
					%> <%=packageDetails.get("maintainer")%> <%
 	} else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-version" />:</td>
				<td>
					<%
						if (packageDetails.containsKey("version") && packageDetails.get("version") != null) {
					%> <%=packageDetails.get("version")%> <%
 	} else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<% */ %>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-created" />:</td>
				<td>
					<%
						if (((JSONObject)packageDetails.get("extras")).containsKey("date_released") && !((JSONObject)packageDetails.get("extras")).get("date_released").equals("")) {
									String dateCreated = ((String) ((JSONObject)packageDetails.get("extras")).get("date_released")).substring(0, 10);
					%> <%=dateCreated%> <%
 	} else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-modified" />:</td>
				<td>
					<%
						if (((JSONObject)packageDetails.get("extras")).containsKey("date_updated") && !((JSONObject)packageDetails.get("extras")).get("date_updated").equals("")) {
									String dateModified = ((String) ((JSONObject)packageDetails.get("extras")).get("date_updated")).substring(0, 10);
									/*SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
									Calendar cal = GregorianCalendar.getInstance();
									cal.set(2011,Integer.parseInt(dateModified.substring(5,2))-1,18);
									dateModified = df.format(cal.getTime());*/
					%> <%=dateModified%> <%
 	} else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-area" />:</td>
				<td>
					<%
						if (((JSONObject)packageDetails.get("extras")).containsKey("geographical_coverage") && !((JSONObject)packageDetails.get("extras")).get("geographical_coverage").equals("")) {
					%> <%=((JSONObject)packageDetails.get("extras")).get("geographical_coverage")%>
					<% 
							if (((JSONObject)packageDetails.get("extras")).containsKey("geographical_granularity") && !((JSONObject)packageDetails.get("extras")).get("geographical_granularity").equals("")) {
						%> (<%=((JSONObject)packageDetails.get("extras")).get("geographical_granularity")%>)
					<%
	 	}
						}  else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-period" />:</td>
				<td>
					<%
						if (((JSONObject)packageDetails.get("extras")).containsKey("temporal_coverage-from") && !((JSONObject)packageDetails.get("extras")).get("temporal_coverage-from").equals(
										"") && ((JSONObject)packageDetails.get("extras")).containsKey("temporal_coverage-to") && !((JSONObject)packageDetails.get("extras")).get("temporal_coverage-to").equals(
										"")) {
					%> <%=((JSONObject)packageDetails.get("extras")).get("temporal_coverage-from")%>
					&ndash; <%=((JSONObject)packageDetails.get("extras")).get("temporal_coverage-to")%>
					<% 
							if (((JSONObject)packageDetails.get("extras")).containsKey("temporal_granularity") && !((JSONObject)packageDetails.get("extras")).get("temporal_granularity").equals(
					"")) {
						%> (<%=((JSONObject)packageDetails.get("extras")).get("temporal_granularity")%>)
					<%
	 	}
						}  else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-license" />:</td>
				<td>
					<%
						if (packageDetails.containsKey("license") && packageDetails.get("license") != null && !packageDetails.get("license").equals("")) {
					%> <%=packageDetails.get("license")%> <%
 	} else {
 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
 	}
 %>
				</td>
			</tr>

			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-categories" />:</td>
				<td>
					<%
						for (int i = 0; i < ((JSONArray) packageDetails.get("groups"))
										.size(); i++) {
									String groupTitle = (String) ((JSONObject)groupsData.get(
											(String)((JSONArray)packageDetails.get("groups")).get(i)
											)).get("title");
									String groupName = (String) ((JSONObject)groupsData.get(
											((JSONArray)packageDetails.get("groups")).get(i)
									)).get("name");
					%><portlet:actionURL var="groupURL">
						<portlet:param name="action" value="packageSearchAction" />
						<portlet:param name="searchquery_groups" value='<%=groupName%>' />
					</portlet:actionURL><a href="<%=groupURL%>"><%=groupTitle%></a><%
						if (i < ((JSONArray)packageDetails.get("groups")).size() - 1) {
					%>, <%
						}
					%> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td><liferay-ui:message key="oc-datasets_metadata-tags" />:</td>
				<td>
					<%
						for (int i = 0; i < ((JSONArray)packageDetails.get("tags"))
										.size(); i++) {
					%><portlet:actionURL var="tagURL">
						<portlet:param name="action" value="packageSearchAction" />
						<portlet:param name="searchquery_tags"
							value='<%=(String)((JSONArray)packageDetails.get("tags")).get(i) %>' />
					</portlet:actionURL><a href="<%=tagURL%>"><%=((JSONArray)packageDetails.get("tags"))
								.get(i)%></a><%
						if (i < ((JSONArray)packageDetails.get("tags")).size() - 1) {
					%>, <%
						}
					%> <%
 	}
 %>
				</td>
			</tr>
			<tr>
				<td valign="top"><liferay-ui:message
						key="oc-datasets_metadata-rating" />:</td>
				<td>
					<%
					//packageRating = 4.5;
						if (packageRating != -1) {
							
							int starsOff = 5;
							int starsOn = (int) Math.floor(packageRating);
							int halfStar = (starsOn < packageRating) ? 1 : 0;
							starsOff = starsOff - starsOn - halfStar;
							for (int i = 1; i <= starsOn; i++) {
								%><img src="<%= request.getContextPath()%>/img/star_on.png"
					class="rating-star" />
					<%								
							}
							if (halfStar == 1) {
								%><img src="<%= request.getContextPath()%>/img/star_half.png"
					class="rating-star" />
					<%								
							}
							for (int i = 1; i <= starsOff; i++) {
								%><img src="<%= request.getContextPath()%>/img/star_off.png"
					class="rating-star" />
					<%								
							}
															
					 	} else {
					 %> <liferay-ui:message key="oc-datasets_metadata-not-available" /> <%
					 	}
 %> 
				</td>
			</tr>
		</table>

		<p>&nbsp;</p>

		<h4>Rate this dataset</h4>

<%
				if (isUserLoggedIn) {
					
					if (packageRatingPermission) {
%>

		<portlet:actionURL var="ratingURL1">
			<portlet:param name="action" value="packageDetailsAction" />
			<portlet:param name="packageId" value='<%=(String)packageDetails.get("id") %>' />
			<portlet:param name="userRating" value="1" />
		</portlet:actionURL>
		<portlet:actionURL var="ratingURL2">
			<portlet:param name="action" value="packageDetailsAction" />
			<portlet:param name="packageId" value='<%=(String)packageDetails.get("id") %>' />
			<portlet:param name="userRating" value="2" />
		</portlet:actionURL>
		<portlet:actionURL var="ratingURL3">
			<portlet:param name="action" value="packageDetailsAction" />
			<portlet:param name="packageId" value='<%=(String)packageDetails.get("id") %>' />
			<portlet:param name="userRating" value="3" />
		</portlet:actionURL>
		<portlet:actionURL var="ratingURL4">
			<portlet:param name="action" value="packageDetailsAction" />
			<portlet:param name="packageId" value='<%=(String)packageDetails.get("id") %>' />
			<portlet:param name="userRating" value="4" />
		</portlet:actionURL>
		<portlet:actionURL var="ratingURL5">
			<portlet:param name="action" value="packageDetailsAction" />
			<portlet:param name="packageId" value='<%=(String)packageDetails.get("id") %>' />
			<portlet:param name="userRating" value="5" />
		</portlet:actionURL>

		<p>
			Add your rating (1&ndash;5 stars):<br />
			<!-- div id="rating-control" style="cursor: pointer; background: #fff; border: 1px solid #c0c2c5; width: 94px; height: 18px;"-->
			<span id="rating-control"> <a href="<%=ratingURL1%>"><img id="rating-star1"
				class="rating-star"
				src="<%= request.getContextPath()%>/img/star_off.png"
				onmouseover="lightStars(1)" onmouseout="dimStars()" /></a><a href="<%=ratingURL2%>"><img
				id="rating-star2" class="rating-star"
				src="<%= request.getContextPath()%>/img/star_off.png"
				onmouseover="lightStars(2)" onmouseout="dimStars()" /></a><a href="<%=ratingURL3%>"><img
				id="rating-star3" class="rating-star"
				src="<%= request.getContextPath()%>/img/star_off.png"
				onmouseover="lightStars(3)" onmouseout="dimStars()" /></a><a href="<%=ratingURL4%>"><img
				id="rating-star4" class="rating-star"
				src="<%= request.getContextPath()%>/img/star_off.png"
				onmouseover="lightStars(4)" onmouseout="dimStars()" /></a><a href="<%=ratingURL5%>"><img
				id="rating-star5" class="rating-star"
				src="<%= request.getContextPath()%>/img/star_off.png"
				onmouseover="lightStars(5)" onmouseout="dimStars()" /></a> <span
				id="rating-text"></span> </span>
		</p>


		<script type="text/javascript">
		function lightStars(count) {
			for (i=1;i<=5;i++) {
				if (i<=count) {
					document.getElementById('rating-star'+i).src = '<%= request.getContextPath()%>/img/star_on.png';
				}
				else {
					document.getElementById('rating-star'+i).src = '<%= request.getContextPath()%>/img/star_off.png';
				}
			}
			if (count > 0) {
				document.getElementById("rating-control").style.width="200px";
				document.getElementById("rating-text").innerHTML = 'Dataset rating: '+count+'/5'; 
			}
			else {
				document.getElementById("rating-control").style.width="94px";
				document.getElementById("rating-text").innerHTML = '';
			}
		}
		function dimStars() {
			lightStars(0);
		}
		</script>

<%
					} else { // user does not have rating permission
%>

		<p>Your rating for this dataset has been recorded &ndash; thanks!</p>

<%
					} 
				} else { // user is not logged in
%>
		<p>Please sign in to submit a rating for this dataset.</p>
<%
				}
%>

		<h4>Discuss this dataset</h4>

		<%
			if (packageCommentsCount != -1) {
		%>
		<p id="packageCommentsLink">
			<a href="#" onclick="javascript:showPackageComments(); return false;">Show
				comments (<%=packageCommentsCount %>)</a> &raquo;
		</p>
		
		<script type="text/javascript">
		function showPackageComments() {
			
			document.getElementById("packageCommentsLink").style.display = "none";
			document.getElementById("packageComments").style.display = "block";
			
		}
		</script>
		
		<div id="packageComments" style="display: none; border: 1px solid #c0c2c5; padding: 1em; margin: 1em 0;">
		
		<% for (int i=0; i<packageComments.size(); i++) { %>
			
				<p style="font-weight: bold;">
				<%=(i+1) %> &ndash; Comment by 
				<% 
					long userId = Long.parseLong( (String) ((JSONObject) packageComments.get(i) ).get("userId"));
				%>
				<%= UserLocalServiceUtil.getUser(userId).getScreenName() %> 
				on 
				<%=((JSONObject) packageComments.get(i)).get("date") %>: </p>
				<p style="margin: 0.5em 1em; padding: 0;"><%=((JSONObject) packageComments.get(i)).get("comment") %></p>
		
		<% } // end for %>
		</div>
		
		<%
			} else {
		%>
		<p>No comments have been submitted so far.</p>
		<%
			} // end if
		%>
		
		<%
				if (isUserLoggedIn) {
		%>
		
		<p>Add your comment:</p>
		
		<form method="post"
				action="<portlet:actionURL name="packageDetailsAction" />">

			<textarea name="userComment" style="width: 100%; height: 80px;"></textarea>
			<input type="hidden" name="packageId" value="<%=(String)packageDetails.get("id") %>" />
			<input type="submit" value="Submit" />

		</form>

<%
				} else { // user is not logged in
%>
		<p>Please sign in to submit a comment for this dataset.</p>
<%
				}
%>		
		
		<%
			}

			// Search results view
			if (containerPackages != null) {
				
				// Insert <hr> if package details are displayed above search results
				if (packageDetails != null) {
					%>
					<hr />
					<%
				}
		%>

		<h3>
			<liferay-ui:icon image="search" />
			<liferay-ui:message key="oc-datasets_search-results" />
			<span style="font-size: 0.8em; font-style: italic;"><%
			String[] param_names = { "q", "title", "notes", "author", "groups", "tags", "filter_by_openness","filter_by_downloadable" };			
			String param_summary = "";
			for (String param : param_names) {
				if (!searchparams.get(param).equals("")) {
					if (param.equals("q"))
						param_summary = param_summary.concat("&quot;" + searchparams.get(param) + "&quot;, ");
					else if (param.equals("filter_by_openness"))
						param_summary = param_summary.concat("&quot;" + LanguageUtil.get(request.getLocale(), "oc-datasets_search-open-license-only") + "&quot;, ");
					else if (param.equals("filter_by_downloadable"))
						param_summary = param_summary.concat("&quot;" + LanguageUtil.get(request.getLocale(), "oc-datasets_search-linked-data-only") + "&quot;, ");
					else
						param_summary = param_summary.concat(LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-" + param) + ": &quot;" + searchparams.get(param) + "&quot;, ");
				}					
			}
			if (param_summary.endsWith(", "))
				param_summary = param_summary.substring(0, param_summary.length()-2);
			if (param_summary.length() > 0)
				param_summary = LanguageUtil.get(request.getLocale(), "oc-datasets_search-results-for") + " " + param_summary;
			%>
			<%=param_summary %>
			</span>
		</h3>

		<p><%=searchhint%></p>

		<liferay-ui:search-iterator paginate=""
			searchContainer="<%=containerPackages %>" />

			<%
				PortletURL clearSearchUrl = renderResponse.createActionURL();
				clearSearchUrl.setParameter("action", "clearSearchAction");	
	 		%>
			<p style="text-align: left; margin-top: 1em;"><input type="button" onclick="location.href='<%=clearSearchUrl %>'" value="<liferay-ui:message key="oc-datasets_search-clear-search" />" /></p>

		<%
			} else if (packageDetails == null) {
		%>

		<!-- h3>
			<liferay-ui:icon image="activate" />
			<liferay-ui:message key="oc-datasets_title" />
		</h3>

		<p>
			<liferay-ui:message key="oc-datasets_intro" />
		</p>
		
		
		<hr -->
		
		
		<% if (isUserDataOwner || isUserDataSteward) {%>
		
		<h3><liferay-ui:message key="oc-datasets_maintain-datasets-heading" /></h3>
		
		<% }
			if (isUserDataSteward) { %>
		
		<p><liferay-ui:message key="oc-datasets_intro-data-steward" /></p>		
						
		<% } 
			if (isUserDataOwner) {
		%>
			
			 
		<p><liferay-ui:message key="oc-datasets_intro-data-owner" /></p>

	
        <liferay-portlet:renderURL
		    portletName="ocmanagedatasetsportlet_WAR_ocmanagedatasetsportlet" plid="<%= managaDatasetsPlId %>"
		    var="createDatasetAction">
		    <liferay-theme:param name="ocAction" value="createMetaDataRender"></liferay-theme:param>
		</liferay-portlet:renderURL>
		
		<form method="post"
                action="<%=createDatasetAction%>" style="display: inline;">
                
        <input type="submit" value="<liferay-ui:message key="oc-datasets_add-new-dataset" />" />
        
        </form>
		
		
		<form method="post"
				action="<portlet:actionURL name="packageSearchAction" />" style="display: inline;">
				
		<input type="submit" value="<liferay-ui:message key="oc-datasets_list-my-datasets" />" /> <input type="hidden" name="searchquery_maintainer" value="<%= themeDisplay.getUser().getFullName() %>" />
		
		</form>
		
		</p>
		
		
		<% } 		
			if (isUserDataOwner || isUserDataSteward) {%>
		
		<hr />
		
		<% } 
		
		%>
		
		
		<h3>
			
			<liferay-ui:message key="oc-datasets_popular-tags" />
		</h3>
		
		
		
			<div style="width:80%;padding: 0 0; margin: -1em auto 1em auto;">
		
		<ol id="tagCloudTopTags" style="width: 90%;">
<% 
if (popularTags != null) {
	
	   for (int i = 0; i < popularTags.size(); i++) { %>
	   <liferay-portlet:actionURL var="popTag">
	   <liferay-portlet:param name="action" value="packageSearchAction"></liferay-portlet:param>
	   <liferay-portlet:param name="searchquery_tags" value="<%=(String)popularTags.get(i) %>"></liferay-portlet:param>
	   </liferay-portlet:actionURL>
	        <li title="<%=(String)popularTags.get(i) %>" value="<%=popularTags.size()-i %>"><a href="<%=popTag %>"><%=(String)popularTags.get(i) %></a></li>
<%     } 
}
%>
		</ol>
		
		</div>
		
		
		<script type="text/javascript">
		
		$("#tagCloudTopTags>li").tsort({order:"rand"});
		$('#tagCloudTopTags').tagcloud({type:"sphere",sizemin:10,sizemax:14,height:100,colormax:"77B238",colormin:"aaa"});
		
		//$('#tagCloudLatestPackages').tagcloud({type:"list",sizemin:10,sizemax:18,height:250});
		
		</script>
		
		
		<hr>
	
		<h3>
				
				<liferay-ui:message key="oc-datasets_latest-datasets" />
			</h3>
		
	
<% 
if (latestPackages != null) {		
	Iterator<String> pIter = latestPackages.keySet().iterator();
	HashMap p;
	
	while (pIter.hasNext()) {
		p = latestPackages.get(pIter.next());
		String dateModified;
		if (((String) p.get("metadata_modified")).equals("")) {
			dateModified = ((String)p.get("metadata_modified")).substring(0, 10);
		}
		else {
			dateModified = ((String)p.get("metadata_created")).substring(0, 10);
		}
		%>
		
		<liferay-portlet:actionURL var="latePkg">
	   <liferay-portlet:param name="action" value="packageDetailsAction"></liferay-portlet:param>
	   <liferay-portlet:param name="packageId" value='<%=(String)p.get("id") %>'></liferay-portlet:param>
	   </liferay-portlet:actionURL>
	   
	        <h4 style="clear: left; margin: 2em 0 .5em 0"><a href="<%=latePkg %>"><%=(String)(String)p.get("title") %></a></h4>
	        <p><%=(String)p.get("notes") %></p>
	        <p><strong><liferay-ui:message key="oc-datasets_metadata-modified" />:</strong> <%=dateModified %></p>
	        <p>
	        
	        <%
	       if (p.containsKey("groups") && p.get("groups") != null && ((JSONArray)p.get("groups"))
					.size() > 0) {
	       %> 
	        
	        <strong><liferay-ui:message key="oc-datasets_metadata-categories" />:</strong> 
	        
	        <%
						for (int i = 0; i < ((JSONArray) p.get("groups"))
										.size(); i++) {
									String groupTitle = (String) ((JSONObject)groupsData.get(
											(String)((JSONArray)p.get("groups")).get(i)
											)).get("title");
									String groupName = (String) ((JSONObject)groupsData.get(
											((JSONArray)p.get("groups")).get(i)
									)).get("name");
					%><portlet:actionURL var="groupURL2">
						<portlet:param name="action" value="packageSearchAction" />
						<portlet:param name="searchquery_groups" value='<%=groupName%>' />
					</portlet:actionURL><a href="<%=groupURL2%>"><%=groupTitle%></a><%
						if (i < ((JSONArray)p.get("groups")).size() - 1) {
					%>, <%
						}
					%> <%
 	}

	}
	        
	       if (p.containsKey("tags") && p.get("tags") != null && ((JSONArray)p.get("tags"))
					.size() > 0) {
	       %> 
	       
	     <strong><liferay-ui:message key="oc-datasets_metadata-tags" />:</strong> 
	    
	    <%
						for (int i = 0; i < ((JSONArray)p.get("tags"))
										.size(); i++) {
					%><portlet:actionURL var="tagURL2">
						<portlet:param name="action" value="packageSearchAction" />
						<portlet:param name="searchquery_tags"
							value='<%=(String)((JSONArray)p.get("tags")).get(i) %>' />
					</portlet:actionURL><a href="<%=tagURL2%>"><%=((JSONArray)p.get("tags"))
								.get(i)%></a><%
						if (i < ((JSONArray)p.get("tags")).size() - 1) {
					%>, <%
						}
					%> <%
 	}
 %>
 
 <% } %>
	    
	    </p>
		<%
		
	}
	 
}
%>
	

<!-- div style="border: 1px solid #ccc; color: #999; padding: 20px;">debug:<p>

<p>

link von anderem portlet: 
<% 

	PortletURL groupUrl = renderResponse.createRenderURL();
	groupUrl.setParameter("groups", "elections");	

%>
<liferay-portlet:renderURL portletName=""></liferay-portlet:renderURL>
<a href="<%=groupUrl %>">'elections' group</a>

</div -->

		<%
			}
		%>

	</div>
	<br style="clear:both;" />
</div>	

	<script type="text/javascript">
function searchMode(mode) {
	if (mode == "extended" || mode == "tag" || mode == "group") {
		// Extended mode - enable link to switch to simple mode
		document.getElementById("searchModeLinkSimple").style.display = "block";
		document.getElementById("searchModeLinkExtended").style.display = "none";
		
		// Display extended form
		document.getElementById("searchFormSimple").style.display = "none";
		document.getElementById("searchFormExtended").style.display = "block";

		// Reset simple query
		document.getElementById("queryfield").value = "";
	}
	else {
		// Simple mode - enable link to switch to extended mode
		document.getElementById("searchModeLinkExtended").style.display = "block";
		document.getElementById("searchModeLinkSimple").style.display = "none";
		
		// Display simple form
		document.getElementById("searchFormExtended").style.display = "none";
		document.getElementById("searchFormSimple").style.display = "block";
		
		// Reset extended parameters
		document.getElementById("titlefield").value = "";
		document.getElementById("notesfield").value = "";
		document.getElementById("authorfield").value = "";
		document.getElementById("maintainerfield").value = "";
		document.getElementById("groupsfield").value = "";
		document.getElementById("tagsfield").value = "";
		document.getElementById("opennessfield").checked = "";
		document.getElementById("downloadablefield").checked = "";
	}
}


	searchMode('<%= searchMode %>');
	</script>

</aui:layout>