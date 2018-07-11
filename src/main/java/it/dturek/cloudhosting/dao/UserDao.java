package it.dturek.cloudhosting.dao;

import it.dturek.cloudhosting.domain.jpa.User;

public interface UserDao {

    User findByEmail(String email);

    User create(User user);

    User update(User user);

    User findByRegistrationKey(String key);

}
