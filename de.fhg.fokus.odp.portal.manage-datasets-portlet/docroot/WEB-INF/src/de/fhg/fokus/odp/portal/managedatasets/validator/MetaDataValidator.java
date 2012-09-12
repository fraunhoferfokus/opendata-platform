package de.fhg.fokus.odp.portal.managedatasets.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.language.LanguageUtil;

import de.fhg.fokus.odp.portal.managedatasets.domain.MetaDataBean;
import de.fhg.fokus.odp.portal.managedatasets.utils.LocaleUtils;
import de.fhg.fokus.odp.portal.managedatasets.utils.ValidationUtilsOc;

// TODO: Auto-generated Javadoc
/**
 * The Class MetaDataValidator.
 */
@Component("metaDataValidator")
public class MetaDataValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class<?> clazz) {
		MetaDataBean.class.isAssignableFrom(clazz);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
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
		
		String temporal_coverage_from = metaData.getTemporal_coverage_from().trim();
		if (!temporal_coverage_from.isEmpty()
				&& !ValidationUtilsOc.validDate(temporal_coverage_from)) {
					errors.rejectValue(
						"temporal_coverage_from", "",
						LanguageUtil.get(
								LocaleUtils.getLocale(), "oc_validation-date")
					);
		}

		String temporal_coverage_to = metaData.getTemporal_coverage_to().trim();
		if (!temporal_coverage_to.isEmpty()
				&& !ValidationUtilsOc.validDate(temporal_coverage_to)) {
					errors.rejectValue(
						"temporal_coverage_to", "",
						LanguageUtil.get(
								LocaleUtils.getLocale(), "oc_validation-date")
					);
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
