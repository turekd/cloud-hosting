package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.dao.UserDao;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.form.MyAccountForm;
import it.dturek.cloudhosting.form.UserCreateForm;
import it.dturek.cloudhosting.service.ApplicationService;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.MailService;
import it.dturek.cloudhosting.service.UserService;
import it.dturek.cloudhosting.util.Md5Util;
import it.dturek.cloudhosting.util.PasswordEncoderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserServiceImpl implements UserService {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private static final long REGISTRATION_KEY_VALID_TO = 172800; // 48 hours

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private MailService mailService;

    @Autowired
    private InternationalizationService internationalizationService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails user = userDao.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", email));
        }
        return user;
    }


    @Override
    public User create(UserCreateForm form) {
        long now = System.currentTimeMillis();

        // Prepare
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoderUtil.encode(form.getPassword()));
        user.setSpaceAvailable(applicationService.getAvailableSpacePerUser());
        user.setCreatedAt(new Timestamp(now));

        // Create key and sent confirmation email
        String key = createRegistrationKey(user);
        user.setRegistrationKey(key);
        user.setRegistrationKeyValidTo(new Timestamp(now + REGISTRATION_KEY_VALID_TO));
        mailService.sendRegistratonMail(user);

        // Save to db
        userDao.create(user);

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User update(User user, MyAccountForm form) {
        user.setEmail(form.getEmail());
        if (!form.getPassword().isEmpty()) {
            user.setPassword(passwordEncoderUtil.encode(form.getPassword()));
        }
        return userDao.update(user);
    }

    @Override
    public User update(User user) {
        return userDao.update(user);
    }

    @Override
    public void addUsedSpace(User user, long bytes) {
        user.setSpaceUsed(user.getSpaceUsed() + bytes);
        update(user);
    }

    @Override
    public void subtractUsedSpace(User user, long bytes) {
        user.setSpaceUsed(user.getSpaceUsed() - bytes);
        update(user);
    }

    @Override
    public String getUserRegisterConfirmationLink(User user) {
        return applicationService.getBaseUrl()
                + internationalizationService.getMessage("register.confirmation_url")
                + "/"
                + user.getRegistrationKey();
    }

    @Override
    public User findByRegistrationKey(String key) {
        return userDao.findByRegistrationKey(key);
    }

    private String createRegistrationKey(User user) {
        long now = System.currentTimeMillis();
        String value = user.getUsername() + "confirmation" + now;
        return Md5Util.getHash(value);
    }
}
