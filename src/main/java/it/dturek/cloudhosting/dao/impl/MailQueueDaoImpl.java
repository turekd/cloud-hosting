package it.dturek.cloudhosting.dao.impl;

import it.dturek.cloudhosting.dao.MailQueueDao;
import it.dturek.cloudhosting.domain.jpa.MailQueue;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class MailQueueDaoImpl implements MailQueueDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void add(MailQueue mailQueue) {
        entityManager.persist(mailQueue);
    }

    @Override
    public List<MailQueue> findAllNotProcessed() {
        Query query = entityManager.createQuery("SELECT m FROM MailQueue m WHERE m.processedAt IS NULL");
        return query.getResultList();
    }

    @Override
    @Transactional
    public void update(MailQueue mailQueue) {
        entityManager.merge(mailQueue);
    }
}
