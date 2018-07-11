package it.dturek.cloudhosting.domain;

import it.dturek.cloudhosting.domain.jpa.ResourceShare;

public class ResourceShareSuccess {

    private ResourceShare resourceShare;
    private String message;

    public ResourceShare getResourceShare() {
        return resourceShare;
    }

    public void setResourceShare(ResourceShare resourceShare) {
        this.resourceShare = resourceShare;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
