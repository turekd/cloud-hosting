package it.dturek.cloudhosting.service;

import it.dturek.cloudhosting.domain.jpa.User;

public interface MailService {

    void sendRegistratonMail(User user);

}
