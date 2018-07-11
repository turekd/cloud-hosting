package it.dturek.cloudhosting.dao;

import it.dturek.cloudhosting.domain.Sort;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ResourceDao {

    Resource create(Resource resource);

    @Cacheable
    Resource find(Long id, User user);

    Resource find(Long id);

    Resource update(Resource resource);

    void delete(Resource resource);

    @Cacheable
    List<Resource> findResources(Resource parent, User user, Sort sort, Integer offset);

    @Cacheable
    List<Resource> findResources(Resource parent, User user, Sort sort);

    @Cacheable
    List<Resource> findResources(Resource parent, User user);

    Resource findByName(String name, Resource parent, User user, Resource.Type type);

    List<Resource> search(User user, String name);

    int countResources(Resource directory, User user);

}
