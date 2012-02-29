package org.opencities.berlin.opendata.portlet.spring.managedatasets.validator;

import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.MetaDataBean;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.LocaleUtils;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.ValidationUtilsOc;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.language.LanguageUtil;

@Component("metaDataValidator")
public class MetaDataValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		MetaDataBean.class.isAssignableFrom(clazz);
		return false;
	}

	public void validate(Object obj, Errors errors) {
		MetaDataBean metaData = (MetaDataBean) obj;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "author_email", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "license_id", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "date_released", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));

		if (metaData.getGroups() == null) {
			errors.rejectValue("groups", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-required"));
		}

		if (!metaData.getUrl().isEmpty()
				&& !ValidationUtilsOc.validUrl(metaData.getUrl())) {
			errors.rejectValue("url", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-url"));
		}

		if (!ValidationUtilsOc.validEmail(metaData.getAuthor_email())) {
			errors.rejectValue("author_email", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-email"));
		}

		if (!ValidationUtilsOc.validPackageName(metaData.getName())) {
			errors.rejectValue("name", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-name"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getAuthor())) {
			errors.rejectValue("author", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getAuthor_email())) {
			errors.rejectValue("author_email", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData
				.getGeographical_coverage())) {
			errors.rejectValue("geographical_coverage", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData
				.getGeographical_granularity())) {
			errors.rejectValue("geographical_granularity", "", LanguageUtil
					.get(LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getName())) {
			errors.rejectValue("name", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getNotes())) {
			errors.rejectValue("notes", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getOthers())) {
			errors.rejectValue("others", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getTags())) {
			errors.rejectValue("tags", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData
				.getTemporal_granularity())) {
			errors.rejectValue("temporal_granularity", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getTitle())) {
			errors.rejectValue("title", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		if (!ValidationUtilsOc.validStandardLength(metaData.getVersion())) {
			errors.rejectValue("version", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-too-long"));
		}

		// for (int i = 0; i <= metaData.getResources().size(); i++) {
		// Resource resource = metaData.getResources().get(i);
		// if (!ValidationUtilsOc.validStandardLength(resource
		// .getDescription())) {
		// errors.rejectValue("metaData.resources[" + i + "].description",
		// LanguageUtil.get(LocaleUtils.getLocale(),
		// "oc_validation-too-long"));
		// }
		//
		// if (!ValidationUtilsOc.validStandardLength(resource.getFormat())) {
		// errors.rejectValue("metaData.resources[" + i + "].format",
		// LanguageUtil.get(LocaleUtils.getLocale(),
		// "oc_validation-too-long"));
		// }
		//
		// if (!ValidationUtilsOc.validStandardLength(resource.getUrl())) {
		// errors.rejectValue("metaData.resources[" + i + "].url",
		// LanguageUtil.get(LocaleUtils.getLocale(),
		// "oc_validation-too-long"));
		// }
		//
		// if (!ValidationUtilsOc.validUrl(resource.getUrl())) {
		// errors.rejectValue("metaData.resources[" + i + "].url",
		// LanguageUtil.get(LocaleUtils.getLocale(),
		// "oc_validation-url"));
		// }
		// }

	}
}
