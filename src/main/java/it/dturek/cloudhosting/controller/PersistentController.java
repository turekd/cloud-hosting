package it.dturek.cloudhosting.controller;

import it.dturek.cloudhosting.domain.AuthenticationError;
import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.form.LoginForm;
import it.dturek.cloudhosting.form.UserCreateForm;
import it.dturek.cloudhosting.form.validator.UserCreateFormValidator;
import it.dturek.cloudhosting.service.ApplicationService;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.MenuService;
import it.dturek.cloudhosting.service.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PersistentController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(PersistentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserCreateFormValidator userCreateFormValidator;

    @Autowired
    private InternationalizationService internationalizationService;

    @Autowired
    private MenuService pageService;

    @Autowired
    private ApplicationService applicationService;

    @InitBinder("userCreateForm")
    protected void initUserCreateFormValidatorBinder(final WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @GetMapping("/register")
    public String register(Model model, UserCreateForm userCreateForm) {
        model.addAttribute("userCreateForm", userCreateForm);
        return "persistent/register";
    }

    @PostMapping("/register")
    public String registerPost(Model model, @Valid UserCreateForm userCreateForm, BindingResult result, RedirectAttributes redirectAttributes) {
        if(!applicationService.isRegistrationEnabled()){
            redirectAttributes.addFlashAttribute("error", internationalizationService.getMessage("register.not_enabled"));
            return "redirect:/register";
        }

        if (!result.hasErrors()) {
            userService.create(userCreateForm);
            redirectAttributes.addFlashAttribute("success", internationalizationService.getMessage("register.success_text"));
            return "redirect:/login";
        }

        model.addAttribute("userCreateForm", userCreateForm);
        model.addAttribute("errors", true);
        return "persistent/register";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(name = "error", required = false) String errorCode) {
        try {
            Optional.ofNullable(AuthenticationError.fromCode(Integer.valueOf(errorCode)))
                    .ifPresent(e -> model.addAttribute("authenticationError", e.getMessage()));
        } catch (NumberFormatException e) {
            model.addAttribute("authenticationError", null);
        }
        model.addAttribute("loginForm", new LoginForm());
        return "persistent/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            Cookie cookie = new Cookie("remember-me", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/confirm/{key}")
    public String registrationConfirm(@PathVariable("key") String key, RedirectAttributes redirectAttributes) {
        User user = userService.findByRegistrationKey(key);
        if (user != null) {
            user.setEnabled(true);
            userService.update(user);
            redirectAttributes.addFlashAttribute("success", internationalizationService.getMessage("registration.confirm_success"));
        } else {
            redirectAttributes.addFlashAttribute("error", internationalizationService.getMessage("registration.confirm_error"));
        }
        return "redirect:/";
    }

}
