package com.jobs.jobsearch.controller;

import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/addUser")
    public @ResponseBody HttpStatus addNewUser (@RequestParam String userName, @RequestParam Integer userRole,
                                                @RequestParam String userEmail, @RequestParam String userHashPassword) {
        User user = new User();
        user.setUserName(userName);
        if (userRole.equals(0))
            user.setRole(UserRole.JOB_SEEKER);
        else if (userRole.equals(1))
            user.setRole(UserRole.COMPANY_ADMIN);
        else
            user.setRole(null);
        user.setEmail(userEmail);
        user.setPassword(userHashPassword);
        userRepository.save(user);
        return HttpStatus.ACCEPTED;
    }

    @GetMapping(path="/allUser")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
