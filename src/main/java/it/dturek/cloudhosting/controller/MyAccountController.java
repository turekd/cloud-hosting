package it.dturek.cloudhosting.controller;

import it.dturek.cloudhosting.domain.jpa.User;
import it.dturek.cloudhosting.form.MyAccountForm;
import it.dturek.cloudhosting.form.validator.MyAccountFormValidator;
import it.dturek.cloudhosting.service.InternationalizationService;
import it.dturek.cloudhosting.service.MenuService;
import it.dturek.cloudhosting.service.SecurityService;
import it.dturek.cloudhosting.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/my-account")
public class MyAccountController {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(MyAccountController.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Autowired
    private MyAccountFormValidator myAccountFormValidator;

    @Autowired
    private InternationalizationService internationalizationService;

    @Autowired
    private MenuService pageService;

    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.addValidators(myAccountFormValidator);
    }

    @GetMapping
    public String showForm(Model model) {
        User user = securityService.getUser();
        MyAccountForm myAccountForm = new MyAccountForm();
        myAccountForm.setEmail(user.getEmail());
        model.addAttribute("myAccountForm", myAccountForm);
        return "myaccount/form";
    }

    @PostMapping
    public String processForm(Model model, @Valid MyAccountForm myAccountForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("myAccountForm", myAccountForm);
            return "myaccount/form";
        }

        User user = securityService.getUser();
        if (!user.getEmail().equals(myAccountForm.getEmail()) || !myAccountForm.getPassword().isEmpty()) {
            userService.update(user, myAccountForm);
            redirectAttributes.addFlashAttribute("success", internationalizationService.getMessage("myaccount.form.success_message"));
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("info", internationalizationService.getMessage("myaccount.form.no_changes"));
        return "redirect:/my-account";
    }

}
