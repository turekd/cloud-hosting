package it.dturek.cloudhosting.dao.impl;

import it.dturek.cloudhosting.dao.UserDao;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.util.DaoUtil;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findByEmail(String email) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE email = :email");
        query.setParameter("email", email);
        return (User) DaoUtil.getSingleResultOrNull(query);
    }

    @Override
    @Transactional
    public User create(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    @Transactional
    public User update(User user) {
        entityManager.merge(user);
        return user;
    }

    @Override
    public User findByRegistrationKey(String key) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE registrationKey = :registrationKey AND registrationKeyValidTo >= NOW()");
        query.setParameter("registrationKey", key);
        return (User) DaoUtil.getSingleResultOrNull(query);
    }
}
