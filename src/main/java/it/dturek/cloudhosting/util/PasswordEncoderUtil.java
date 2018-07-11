package it.dturek.cloudhosting.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class PasswordEncoderUtil extends BCryptPasswordEncoder {

    private static final int STRENGTH = 4;

    public PasswordEncoderUtil() {
        super(STRENGTH);
    }
}