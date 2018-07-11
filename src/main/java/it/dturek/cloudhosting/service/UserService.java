package it.dturek.cloudhosting.service;

import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.form.MyAccountForm;
import it.dturek.cloudhosting.form.UserCreateForm;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User create(UserCreateForm form);

    User findByEmail(String email);

    User update(User user, MyAccountForm form);

    User update(User user);

    void addUsedSpace(User user, long bytes);

    void subtractUsedSpace(User user, long bytes);

    String getUserRegisterConfirmationLink(User user);

    User findByRegistrationKey(String key);
}
