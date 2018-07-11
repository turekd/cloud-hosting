package it.dturek.cloudhosting.config;

import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@Component
public class AuthenticationSuccessHandlerImpl extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        User user = (User) userService.loadUserByUsername(authentication.getName());
        user.setLastLoggedAt(new Timestamp(System.currentTimeMillis()));
        userService.update(user);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}