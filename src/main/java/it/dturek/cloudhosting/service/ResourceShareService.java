package it.dturek.cloudhosting.service;

import it.dturek.cloudhosting.domain.ResourceShareSuccess;
import it.dturek.cloudhosting.domain.ResourceShareType;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.ResourceShare;

public interface ResourceShareService {

    ResourceShareSuccess add(Resource resource, ResourceShareType type);

    ResourceShare findByKey(String key);

    boolean isShareable(ResourceShare resourceShare);

    void handleAfterShare(ResourceShare resourceShare);

}
