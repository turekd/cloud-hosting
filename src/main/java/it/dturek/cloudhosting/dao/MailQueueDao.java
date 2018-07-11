package it.dturek.cloudhosting.dao;

import it.dturek.cloudhosting.domain.jpa.MailQueue;

import java.util.List;

public interface MailQueueDao {

    void add(MailQueue mailQueue);

    List<MailQueue> findAllNotProcessed();

    void update(MailQueue mailQueue);

}
