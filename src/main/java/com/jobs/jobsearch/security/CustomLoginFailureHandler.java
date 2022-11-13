package com.jobs.jobsearch.security;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        User user = userService.findByUsername(username);
        String loginErrorMessage = "Username and password is not valid";

        if (user != null) {
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS ) {
                    if( user.getFailedAttempt()+1==UserService.MAX_FAILED_ATTEMPTS ){
                        loginErrorMessage = "Only one more attempt is allowed";
                    }
                    userService.increaseFailedAttempts(user);
                } else {
                    userService.lock(user);
                    loginErrorMessage = "User has been locked for 24 hours";
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            } else if (!user.isAccountNonLocked()) {
                if (userService.unlockWhenTimeExpired(user)) {
                    loginErrorMessage = "user account has been unlocked";
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            }

        }
        response.sendRedirect("/login?loginError="+loginErrorMessage);
//        super.setDefaultFailureUrl("/login?error");
//        super.onAuthenticationFailure(request, response, exception);
    }

}
