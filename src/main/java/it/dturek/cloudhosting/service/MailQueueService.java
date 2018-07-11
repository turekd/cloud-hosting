package it.dturek.cloudhosting.service;

import it.dturek.cloudhosting.domain.jpa.MailQueue;

public interface MailQueueService {

    void add(MailQueue mailQueue);

    void processQueue();

}
