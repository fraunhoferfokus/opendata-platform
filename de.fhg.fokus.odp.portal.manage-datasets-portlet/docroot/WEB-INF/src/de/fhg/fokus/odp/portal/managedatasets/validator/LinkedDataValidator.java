package de.fhg.fokus.odp.portal.managedatasets.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.language.LanguageUtil;

import de.fhg.fokus.odp.portal.managedatasets.domain.LinkedData;
import de.fhg.fokus.odp.portal.managedatasets.domain.MetaDataBean;
import de.fhg.fokus.odp.portal.managedatasets.utils.LocaleUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class LinkedDataValidator.
 */
@Component("linkedDataValidator")
public class LinkedDataValidator implements Validator {

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
