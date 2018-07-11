package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.service.InternationalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class InternationalizationServiceImpl implements InternationalizationService {

    @Autowired
    private MessageSource messageSource;

    private Locale locale;

    public InternationalizationServiceImpl() {
        locale = LocaleContextHolder.getLocale();
    }

    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
