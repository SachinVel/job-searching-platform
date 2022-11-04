package com.jobs.jobsearch.controller;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.security.SecurityService;
import com.jobs.jobsearch.model.VerificationToken;
import com.jobs.jobsearch.service.UserService;
import com.jobs.jobsearch.util.EmailSender;
import com.jobs.jobsearch.validator.UserValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


@Controller
public class AuthController {

    private static final Logger LOGGER= LoggerFactory.getLogger(AuthController.class);

    private UserService userService;
    @Autowired
    private UserValidator userValidator;

    @Autowired
    private SecurityService securityService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String home(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(authentication.getAuthorities());
        if( list.get(0).getAuthority().equals("JOB_SEEKER") ){
            return "redirect:/seeker/index";
        }else{
            return "redirect:/company/index";
        }

    }

    @GetMapping("/login")
    public String showLoginPage(Model model, String error, String logout){
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        // create model object to store form data
        model.addAttribute("userForm", new User());
        LOGGER.info("Came inside get register");
        return "register";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute("userForm") User userForm, Model model, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try{

            User curUser = userService.save(userForm);
            String token = UUID.randomUUID().toString();
            userService.createVerificationToken(curUser, token);
            String confirmationUrl
                    = "https://localhost/register/confirm?token=" + token;
            String message = "Click this link to verify your email : "+confirmationUrl;
            EmailSender emailSender = new EmailSender();
            emailSender.sendConfirmationLink(curUser,message);

//            securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());
        }catch (UserAlreadyExistException userAlreadyExistException){
            model.addAttribute("message", "An account for that username/email already exists.");
            return "register";
        }

        return "redirect:/register/emailConfirmation";
    }

    @GetMapping("/register/emailConfirmation")
    public String showEmailConfirmationPage(Model model){
        return "email-confirmation";
    }

    @GetMapping("/register/confirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) throws UserAlreadyExistException{

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = "auth invalid token";
            model.addAttribute("message", message);
            return "redirect:/badUser";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = "auth token is expired";
            model.addAttribute("message", messageValue);
            return "redirect:/badUser";
        }

        user.setEnabled(true);
        userService.updateUser(user);
        userService.deleteVerificationToken(verificationToken);
        model.addAttribute("message", "Email has been verified successfully");
        return "login";
    }

}
