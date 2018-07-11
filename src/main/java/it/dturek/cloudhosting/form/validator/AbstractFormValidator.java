package it.dturek.cloudhosting.form.validator;

import it.dturek.cloudhosting.form.validator.rule.CustomValidatorRule;
import it.dturek.cloudhosting.form.validator.rule.UniqueValidatorRule;
import it.dturek.cloudhosting.service.InternationalizationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.util.List;

public abstract class AbstractFormValidator implements Validator {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(AbstractFormValidator.class);

    @Autowired
    private InternationalizationService internationalizationService;

    void rejectField(Errors errors, String fieldName, String message) {
        rejectField(errors, fieldName, new Object[]{}, message);
    }

    void rejectField(Errors errors, String fieldName, Object[] args, String message) {
        String errorCode = "error." + fieldName;
        if (errors.hasFieldErrors(fieldName)) {
            return;
        }
        if (!isAlreadyRejected(errors, errorCode)) {
            errors.rejectValue(fieldName, errorCode, args, message);
        }
    }

    Rule createRule(Errors errors, String field, String value) {
        return new Rule(errors, field, value);
    }

    private boolean isAlreadyRejected(Errors errors, String errorCode) {
        List<ObjectError> globalErrors = errors.getGlobalErrors();
        boolean errorCodeExists = false;
        for (ObjectError e : globalErrors) {
            if (errorCode.equals(e.getCode())) {
                errorCodeExists = true;
                break;
            }
        }
        return errorCodeExists;
    }

    class Rule {

        private static final String MESSAGE_ERROR_FORM_EMPTY = "error.form.empty";
        private static final String MESSAGE_ERROR_MIN_LENGTH = "error.form.min_length";
        private static final String MESSAGE_ERROR_MAX_LENGTH = "error.form.max_length";
        private static final String MESSAGE_ERROR_NOT_EMAIL = "error.form.not_email";
        private static final String MESSAGE_ERROR_NOT_UNIQUE = "error.form.not_unique";

        private Errors errors;
        private String field;
        private String value;

        private Rule(Errors errors, String field, String value) {
            this.errors = errors;
            this.field = field;
            this.value = value;
        }

        Rule isNotEmpty() {
            if (StringUtils.isEmpty(value)) {
                rejectField(errors, field, internationalizationService.getMessage(MESSAGE_ERROR_FORM_EMPTY));
            }
            return this;
        }

        Rule isMinimumLength(int minLength) {
            if (!value.isEmpty() && value.length() < minLength) {
                rejectField(errors, field, new Object[]{minLength}, internationalizationService.getMessage(MESSAGE_ERROR_MIN_LENGTH));
            }
            return this;
        }

        Rule isMinimumLength(String messageLength) {
            return isMinimumLength(Integer.valueOf(internationalizationService.getMessage(messageLength)));
        }

        Rule isMaximumLength(int maxLength) {
            if (value.length() > maxLength) {
                rejectField(errors, field, new Object[]{maxLength}, internationalizationService.getMessage(MESSAGE_ERROR_MAX_LENGTH));
            }
            return this;
        }

        Rule isMaximumLength(String messageLength) {
            return isMaximumLength(Integer.valueOf(internationalizationService.getMessage(messageLength)));
        }

        Rule isEqualTo(String valueToCompare, String messageCode) {
            if (!value.equals(valueToCompare)) {
                rejectField(errors, field, internationalizationService.getMessage(messageCode));
            }
            return this;
        }

        Rule isEmailAddress() {
            if (!EmailValidator.getInstance().isValid(value)) {
                rejectField(errors, field, internationalizationService.getMessage(MESSAGE_ERROR_NOT_EMAIL));
            }
            return this;
        }

        Rule isUnique(UniqueValidatorRule rule) {
            return isUnique(rule, MESSAGE_ERROR_NOT_UNIQUE);
        }

        Rule isUnique(UniqueValidatorRule rule, String message) {
            if (!value.isEmpty() && !rule.isUnique(value)) {
                rejectField(errors, field, internationalizationService.getMessage(message));
            }
            return this;
        }

        Rule customRule(CustomValidatorRule rule, String message) {
            if (!rule.isValid()) {
                rejectField(errors, field, internationalizationService.getMessage(message));
            }
            return this;
        }

    }

}
