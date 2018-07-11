package it.dturek.cloudhosting.dao;

import it.dturek.cloudhosting.domain.jpa.ResourceShare;

public interface ResourceShareDao {

    ResourceShare add(ResourceShare resourceShare);

    ResourceShare findByKey(String key);

    void delete(ResourceShare resourceShare);

}
