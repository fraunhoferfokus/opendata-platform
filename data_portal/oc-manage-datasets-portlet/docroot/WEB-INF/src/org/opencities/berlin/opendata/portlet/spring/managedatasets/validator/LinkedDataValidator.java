package org.opencities.berlin.opendata.portlet.spring.managedatasets.validator;

import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.LinkedData;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.MetaDataBean;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.LocaleUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.language.LanguageUtil;

@Component("linkedDataValidator")
public class LinkedDataValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		MetaDataBean.class.isAssignableFrom(clazz);
		return false;
	}

	public void validate(Object obj, Errors errors) {
		LinkedData linkedData = (LinkedData) obj;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language", "",
				LanguageUtil.get(LocaleUtils.getLocale(),
						"oc_validation-required"));

		if (!linkedData.isValidFile()) {
			errors.rejectValue("validFile", "", LanguageUtil.get(
					LocaleUtils.getLocale(), "oc_validation-file"));
		}

	}
}
