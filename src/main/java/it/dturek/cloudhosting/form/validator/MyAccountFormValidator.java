package it.dturek.cloudhosting.form.validator;

import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.form.MyAccountForm;
import it.dturek.cloudhosting.service.SecurityService;
import it.dturek.cloudhosting.service.UserService;
import it.dturek.cloudhosting.util.PasswordEncoderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class MyAccountFormValidator extends AbstractFormValidator {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(MyAccountFormValidator.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PasswordEncoderUtil passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return MyAccountForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MyAccountForm form = (MyAccountForm) o;
        User user = securityService.getUser();

        createRule(errors, "email", form.getEmail())
                .isNotEmpty()
                .isEmailAddress()
                .isMinimumLength("validation.email.min_length")
                .isMaximumLength("validation.email.max_length")
                .isUnique(value -> userService.findByEmail(form.getEmail()) == null || user.getEmail().equals(value),
                        "error.form.email_not_unique");

        createRule(errors, "password", form.getPassword())
                .isMinimumLength("validation.password.min_length")
                .isMaximumLength("validation.password.max_length");

        createRule(errors, "passwordRepeat", form.getPasswordRepeat())
                .isEqualTo(form.getPassword(), "register.form.error.password2_not_equal");

        createRule(errors, "currentPassword", form.getCurrentPassword())
                .isNotEmpty()
                .customRule(() -> passwordEncoder.matches(form.getCurrentPassword(), user.getPassword()),
                        "form.error.currentPassword");

        createRule(errors, "password", form.getPassword())
                .customRule(() -> !passwordEncoder.matches(form.getPassword(), user.getPassword()),
                        "myaccount.form.newPassword_equal_to_old");


    }
}
