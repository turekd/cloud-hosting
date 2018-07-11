package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.dao.MailQueueDao;
import it.dturek.cloudhosting.domain.jpa.MailQueue;
import it.dturek.cloudhosting.service.MailQueueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class MailQueueServiceImpl implements MailQueueService {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(MailQueueServiceImpl.class);

    @Autowired
    private MailQueueDao mailQueueDao;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void add(MailQueue mailQueue) {
        mailQueue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        mailQueueDao.add(mailQueue);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void processQueue() {
        mailQueueDao.findAllNotProcessed().forEach(this::processQueue);
    }

    private void processQueue(MailQueue mailQueue) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sending email {}", mailQueue);
        }
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(mailQueue.getRecipent());
            message.setSubject(mailQueue.getSubject());
            message.setText(mailQueue.getBody(), true);
        };
        mailSender.send(preparator);
        mailQueue.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        mailQueueDao.update(mailQueue);
    }
}
