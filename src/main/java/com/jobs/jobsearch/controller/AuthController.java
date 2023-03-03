package com.jobs.jobsearch.controller;

import antlr.StringUtils;
import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.security.SecurityService;
import com.jobs.jobsearch.service.UserService;
import com.jobs.jobsearch.util.EmailSender;
import com.jobs.jobsearch.validator.UserValidator;
import jakarta.validation.Valid;
import org.apache.commons.text.StringEscapeUtils;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/")
    public String redirectFromhHome(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(authentication.getAuthorities());
        if( list.get(0).getAuthority().equals("JOB_SEEKER") ){
            return "redirect:/seeker/index#job";
        }else{
            return "redirect:/company/index#add-job";
        }

    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String home(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(authentication.getAuthorities());
        if( list.get(0).getAuthority().equals("JOB_SEEKER") ){
            return "redirect:/seeker/index#job";
        }else{
            return "redirect:/company/index#add-job";
        }

    }

    @GetMapping("/login")
    public String showLoginPage(Model model, String error, String logout){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication!=null ){
            List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(authentication.getAuthorities());
            if( list.get(0).getAuthority().equals("JOB_SEEKER") ){
                return "redirect:/seeker/index";
            }else if(list.get(0).getAuthority().equals("COMPANY_ADMIN")){
                return "redirect:/company/index";
            }
        }

        String loginError = (String)req.getSession().getAttribute("loginError");

        if( loginError!=null ){
            model.addAttribute("error", loginError);
            req.getSession().removeAttribute("loginError");
        }
        if (error != null){
            model.addAttribute("error", "Your username and password is invalid.");
        }
        if (logout != null){
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        // create model object to store form data
        model.addAttribute("userForm", new UserRegistrationForm());
        LOGGER.info("Came inside get register");
        return "register";
    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("userForm") UserRegistrationForm userForm, Model model, BindingResult bindingResult) {

        try{
            userValidator.validateRegistrationForm(userForm,bindingResult);

            if( bindingResult.hasErrors() ) {
                return "register";
            }

            User curUser = userService.saveUser(userForm.getUser());
            if( curUser.role== UserRole.JOB_SEEKER ){
                JobSeekerDetails jobSeekerDetails = userForm.getJobSeeker();
                jobSeekerDetails.setUser(curUser);
                userService.saveJobSeekerDetails(jobSeekerDetails);
            }else{
                Company companyDetails = userForm.getCompany();
                companyDetails.setUser(curUser);
                userService.saveCompanyDetails(companyDetails);
            }
            String token = UUID.randomUUID().toString();
            userService.createVerificationToken(curUser, token);
            String confirmationUrl
                    = "http://ec2-44-203-18-179.compute-1.amazonaws.com:3000/register/confirm?token=" + token;
            String message = "Click this link to verify your email : "+confirmationUrl;
            EmailSender emailSender = new EmailSender();
            emailSender.sendConfirmationLink(curUser,message);

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
