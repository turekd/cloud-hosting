package it.dturek.cloudhosting.form.validator;

import it.dturek.cloudhosting.form.UserCreateForm;
import it.dturek.cloudhosting.service.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UserCreateFormValidator extends AbstractFormValidator {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(UserCreateFormValidator.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateForm form = (UserCreateForm) target;

        createRule(errors, "email", form.getEmail())
                .isNotEmpty()
                .isEmailAddress()
                .isMinimumLength("validation.email.min_length")
                .isMaximumLength("validation.email.max_length")
                .isUnique(value -> userService.findByEmail(form.getEmail()) == null, "error.form.email_not_unique");
        createRule(errors, "password", form.getPassword())
                .isNotEmpty()
                .isMinimumLength("validation.password.min_length")
                .isMaximumLength("validation.password.max_length");
        createRule(errors, "passwordRepeated", form.getPasswordRepeated())
                .isNotEmpty()
                .isEqualTo(form.getPassword(), "register.form.error.password2_not_equal");
    }

}
