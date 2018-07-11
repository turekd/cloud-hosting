package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.domain.jpa.MailQueue;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.MailQueueService;
import it.dturek.cloudhosting.service.MailService;
import it.dturek.cloudhosting.service.UserService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private UserService userService;

    @Autowired
    private MailQueueService mailQueueService;

    @Autowired
    private InternationalizationService internationalizationService;

    @Override
    public void sendRegistratonMail(User user) {
        Map<String, Object> model = new HashMap<>();
        model.put("link", userService.getUserRegisterConfirmationLink(user));
        String text = VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "templates/email/registration_confirmation.html", model);

        MailQueue mailQueue = new MailQueue();
        mailQueue.setBody(text);
        mailQueue.setRecipent(user.getEmail());
        mailQueue.setSubject(internationalizationService.getMessage("mail.registration.subject"));
        mailQueueService.add(mailQueue);
    }

}
