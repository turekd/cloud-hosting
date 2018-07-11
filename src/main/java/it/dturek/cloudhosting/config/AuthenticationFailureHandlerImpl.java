package it.dturek.cloudhosting.config;

import it.dturek.cloudhosting.domain.AuthenticationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationFailureHandlerImpl.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        AuthenticationError authenticationError = AuthenticationError.BAD_CREDENTIALS;
        if (e instanceof DisabledException) {
            authenticationError = AuthenticationError.NOT_ENABLED;
        }
        httpServletResponse.sendRedirect("/login?error=" + authenticationError.getCode());
    }
}
