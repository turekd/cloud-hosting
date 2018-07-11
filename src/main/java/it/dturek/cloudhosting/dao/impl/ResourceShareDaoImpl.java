package it.dturek.cloudhosting.dao.impl;

import it.dturek.cloudhosting.dao.ResourceShareDao;
import it.dturek.cloudhosting.domain.jpa.ResourceShare;
import it.dturek.cloudhosting.util.DaoUtil;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
public class ResourceShareDaoImpl implements ResourceShareDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ResourceShare add(ResourceShare resourceShare) {
        entityManager.persist(resourceShare);
        return null;
    }

    @Override
    public ResourceShare findByKey(String key) {
        Query query = entityManager.createQuery("SELECT r FROM ResourceShare r WHERE key = :key");
        query.setParameter("key", key);
        return (ResourceShare) DaoUtil.getSingleResultOrNull(query);
    }

    @Override
    @Transactional
    public void delete(ResourceShare resourceShare) {
        entityManager.remove(resourceShare);
    }
}
