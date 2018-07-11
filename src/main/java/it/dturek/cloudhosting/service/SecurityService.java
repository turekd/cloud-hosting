package it.dturek.cloudhosting.service;

import it.dturek.cloudhosting.domain.jpa.User;

public interface SecurityService {

    boolean isAuthenticated();

    User getUser();

}
