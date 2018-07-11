package it.dturek.cloudhosting.config;

import it.dturek.cloudhosting.interceptor.GlobalVariablesInterceptor;
import it.dturek.cloudhosting.interceptor.MenuInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Properties;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private GlobalVariablesInterceptor globalVariablesInterceptor;

    @Autowired
    private MenuInterceptor menuInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalVariablesInterceptor);
        registry.addInterceptor(menuInterceptor);
        super.addInterceptors(registry);
    }

    @Bean
    public ViewResolver viewResolver(MessageSource messageSource) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setPrefix("src/main/resources/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.setMessageSource(messageSource);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(engine);
        return viewResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setAlwaysUseMessageFormat(true);
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasenames("classpath:messages");
        return messageSource;
    }

    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setCreateTableOnStartup(false);
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(new ShallowEtagHeaderFilter());
        filterBean.setUrlPatterns(Arrays.asList("*"));
        return filterBean;
    }

    /* ********* Velocity ********* */

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.socketFactory.port}")
    private String smtpSocketFactoryPort;

    @Value("${mail.smtp.socketFactory.class}")
    private String smtpSocketFactoryClass;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.login}")
    private String smtpLogin;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Bean
    public JavaMailSender mailSender() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port", smtpSocketFactoryPort);
        props.put("mail.smtp.socketFactory.class", smtpSocketFactoryClass);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.port", smtpPort);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(props);

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpLogin, smtpPassword);
            }
        });
        mailSender.setSession(session);

        return mailSender;
    }

    @Bean
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom("dturek.test@gmail.com");
        return templateMessage;
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngine() {
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("resource.loader", "class");
        velocityProperties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
        velocityEngineFactoryBean.setVelocityProperties(velocityProperties);
        return velocityEngineFactoryBean;
    }
}
