package it.dturek.cloudhosting.dao.impl;

import it.dturek.cloudhosting.dao.ResourceDao;
import it.dturek.cloudhosting.domain.Sort;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.util.DaoUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ResourceDaoImpl implements ResourceDao {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(ResourceDaoImpl.class);

    private static final String QUERY_FIND_BY_NAME_NULL_PARENT = "" +
            "SELECT r " +
            "FROM Resource r " +
            "WHERE user = :user AND type = :type AND name = :name AND parent IS NULL";

    private static final String QUERY_FIND_BY_NAME_NOT_NULL_PARENT = "" +
            "SELECT r " +
            "FROM Resource r " +
            "WHERE user = :user AND type = :type AND name = :name AND parent = :parent";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Resource create(Resource resource) {
        entityManager.persist(resource);
        return resource;
    }

    @Override
    public Resource find(Long id, User user) {
        Query query = entityManager.createQuery("SELECT r FROM Resource r WHERE id = :id AND user = :user");
        query.setParameter("id", id);
        query.setParameter("user", user);
        return (Resource) DaoUtil.getSingleResultOrNull(query);
    }

    @Override
    public Resource find(Long id) {
        Query query = entityManager.createQuery("SELECT r FROM Resource r WHERE id = :id");
        query.setParameter("id", id);
        return (Resource) DaoUtil.getSingleResultOrNull(query);
    }

    @Override
    @Transactional
    public Resource update(Resource resource) {
        entityManager.merge(resource);
        return resource;
    }

    @Override
    @Transactional
    public void delete(Resource resource) {
        entityManager.remove(resource);
    }

    @Override
    public List<Resource> findResources(Resource parent, User user, Sort sort, Integer offset) {
        String orderColumn;
        switch (sort.getColumn()) {
            case "modification_time":
                orderColumn = "modificationTime";
                break;
            case "size":
                orderColumn = "size";
                break;
            default:
                orderColumn = "name";
        }

        String order = String.format(" ORDER BY %s %s", orderColumn, sort.getOrder());

        Query query;
        if (parent != null) {
            query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent = :parent AND user = :user" + order);
            query.setParameter("parent", parent);
            query.setParameter("user", user);
        } else {
            query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent IS NULL AND user = :user" + order);
            query.setParameter("user", user);
        }

        query.setFirstResult(offset);
        query.setMaxResults(20);

        return query.getResultList();
    }

    @Override
    public List<Resource> findResources(Resource parent, User user, Sort sort) {
        String orderColumn;
        switch (sort.getColumn()) {
            case "modification_time":
                orderColumn = "modificationTime";
                break;
            case "size":
                orderColumn = "size";
                break;
            default:
                orderColumn = "name";
        }

        String order = String.format(" ORDER BY %s %s", orderColumn, sort.getOrder());

        Query query;
        if (parent != null) {
            query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent = :parent AND user = :user" + order);
            query.setParameter("parent", parent);
            query.setParameter("user", user);
        } else {
            query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent IS NULL AND user = :user" + order);
            query.setParameter("user", user);
        }

        query.setMaxResults(20);

        return query.getResultList();
    }

    @Override
    public List<Resource> findResources(Resource parent, User user) {
        Query query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent = :parent AND user = :user");
        query.setParameter("parent", parent);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public Resource findByName(String name, Resource parent, User user, Resource.Type type) {
        Query query;
        if (parent == null) {
            query = entityManager.createQuery(QUERY_FIND_BY_NAME_NULL_PARENT);
        } else {
            query = entityManager.createQuery(QUERY_FIND_BY_NAME_NOT_NULL_PARENT);
            query.setParameter("parent", parent);
        }

        query.setParameter("user", user);
        query.setParameter("type", type);
        query.setParameter("name", name);
        return (Resource) DaoUtil.getSingleResultOrNull(query);
    }

    private static final String QUERY_SEARCH = "" +
            "SELECT r " +
            "FROM Resource r " +
            "WHERE name LIKE :name AND user = :user";

    @Override
    public List<Resource> search(User user, String name) {
        Query query = entityManager.createQuery(QUERY_SEARCH);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public int countResources(Resource directory, User user) {
        Query query;
        if (directory != null) {
            query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent = :parent AND user = :user");
            query.setParameter("parent", directory);
            query.setParameter("user", user);
        } else {
            query = entityManager.createQuery("SELECT r FROM Resource r WHERE parent IS NULL AND user = :user");
            query.setParameter("user", user);
        }

        return query.getResultList().size();
    }
}
