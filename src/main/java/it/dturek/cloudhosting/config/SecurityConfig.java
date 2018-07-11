package it.dturek.cloudhosting.config;

import it.dturek.cloudhosting.service.UserService;
import it.dturek.cloudhosting.util.PasswordEncoderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);
    private static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 86400;

    @Autowired
    private UserService userService;

    @Autowired
    private PersistentTokenRepository jdbcTokenRepository;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/drive/**", "/download", "/api/**", "/my-account").authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(new AuthenticationFailureHandlerImpl())

                .and()
                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenRepository(jdbcTokenRepository)
                .tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS)
                .userDetailsService(userService)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("remember-me");

        http.csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoderUtil);
    }

}
