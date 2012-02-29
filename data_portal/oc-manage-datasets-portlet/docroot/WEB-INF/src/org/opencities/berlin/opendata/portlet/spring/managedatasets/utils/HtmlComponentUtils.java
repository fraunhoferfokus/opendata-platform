package org.opencities.berlin.opendata.portlet.spring.managedatasets.utils;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.ValueLabelEntry;

import com.liferay.portal.kernel.language.LanguageUtil;

public class HtmlComponentUtils {
	public static List<ValueLabelEntry> createLicenses() {
		List<ValueLabelEntry> entries = new ArrayList<ValueLabelEntry>();
		entries.add(new ValueLabelEntry("cc-by",
				"OKD Compliant::Creative Commons Attribution"));
		entries.add(new ValueLabelEntry("cc-by-sa",
				"OKD Compliant::Creative Commons Share-Alike"));
		entries.add(new ValueLabelEntry("apache",
				"OSI Approved::Apache Software License"));
		entries.add(new ValueLabelEntry("apache2.0",
				"OSI Approved::Apache License, 2.0"));
		entries.add(new ValueLabelEntry("bsd-license",
				"OSI Approved::New and Simplified BSD licenses"));
		entries.add(new ValueLabelEntry("ca-tosl1.1",
				"OSI Approved::Computer Associates Trusted Open Source License 1.1"));
		entries.add(new ValueLabelEntry("cc-nc",
				"Non-OKD Compliant::Creative Commons Non-Commercial (Any)"));
		entries.add(new ValueLabelEntry("cc-zero",
				"OKD Compliant::Creative Commons CCZero"));
		entries.add(new ValueLabelEntry("eclipse-1.0",
				"OSI Approved::Eclipse Public License"));
		entries.add(new ValueLabelEntry("gfdl",
				"OKD Compliant::GNU Free Documentation License"));
		entries.add(new ValueLabelEntry("gpl-2.0",
				"OSI Approved::GNU General Public License (GPL)"));
		entries.add(new ValueLabelEntry("gpl-3.0",
				"OSI Approved::GNU General Public License version 3.0 (GPLv3)"));
		entries.add(new ValueLabelEntry("mit-license",
				"OSI Approved::MIT license"));
		entries.add(new ValueLabelEntry("mozilla",
				"OSI Approved::Mozilla Public License 1.0 (MPL)"));
		entries.add(new ValueLabelEntry("mozilla1.1",
				"OSI Approved::Mozilla Public License 1.1 (MPL)"));
		entries.add(new ValueLabelEntry("notspecified",
				"Other::License Not Specified"));
		entries.add(new ValueLabelEntry("odc-odbl",
				"OKD Compliant::Open Data Commons Open Database License (ODbL)"));
		entries.add(new ValueLabelEntry("odc-pddl",
				"OKD Compliant::Open Data Commons Public Domain Dedication and Licence (PDDL)"));
		entries.add(new ValueLabelEntry("W3C", "OSI Approved::W3C License"));
		return entries;
	}

	public static List<ValueLabelEntry> createCategories(PortletRequest request) {
		List<ValueLabelEntry> entries = new ArrayList<ValueLabelEntry>();
		entries.add(new ValueLabelEntry("rec", LanguageUtil.get(
				request.getLocale(), "oc_category-art-recreation")));
		entries.add(new ValueLabelEntry("business", LanguageUtil.get(
				request.getLocale(), "oc_category-business-enterprise")));
		entries.add(new ValueLabelEntry("budget", LanguageUtil.get(
				request.getLocale(), "oc_category-city-budget")));
		entries.add(new ValueLabelEntry("stats", LanguageUtil.get(
				request.getLocale(), "oc_category-city-portal-statistic")));
		entries.add(new ValueLabelEntry("housing", LanguageUtil.get(
				request.getLocale(), "oc_category-construction-housing")));
		entries.add(new ValueLabelEntry("safety", LanguageUtil.get(
				request.getLocale(), "oc_category-crime-safety")));
		entries.add(new ValueLabelEntry("demographics", LanguageUtil.get(
				request.getLocale(), "oc_category-demographics")));
		entries.add(new ValueLabelEntry("edu", LanguageUtil.get(
				request.getLocale(), "oc_category-education")));
		entries.add(new ValueLabelEntry("elections", LanguageUtil.get(
				request.getLocale(), "oc_category-elections")));
		entries.add(new ValueLabelEntry("emergency", LanguageUtil.get(
				request.getLocale(), "oc_category-emergency-services")));
		entries.add(new ValueLabelEntry("energy", LanguageUtil.get(
				request.getLocale(), "oc_category-energy-utilities")));
		entries.add(new ValueLabelEntry("environment", LanguageUtil.get(
				request.getLocale(), "oc_category-environment-geography")));
		entries.add(new ValueLabelEntry("health", LanguageUtil.get(
				request.getLocale(), "oc_category-health-disability")));
		entries.add(new ValueLabelEntry("employment", LanguageUtil.get(
				request.getLocale(), "oc_category-employment")));
		entries.add(new ValueLabelEntry("law", LanguageUtil.get(
				request.getLocale(), "oc_category-law")));
		entries.add(new ValueLabelEntry("politics", LanguageUtil.get(
				request.getLocale(), "oc_category-political")));
		entries.add(new ValueLabelEntry("tourism", LanguageUtil.get(
				request.getLocale(), "oc_category-tourism")));
		entries.add(new ValueLabelEntry("transport", LanguageUtil.get(
				request.getLocale(), "oc_category-urban-transport")));
		entries.add(new ValueLabelEntry("misc.", LanguageUtil.get(
				request.getLocale(), "oc_category-misc")));
		return entries;
	}
}
