package com.jobs.jobsearch.service;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.VerificationToken;

public interface UserService {
    User save(User user) throws UserAlreadyExistException;

    void updateUser(User user);

    User findByUsername(String username);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    void deleteVerificationToken(VerificationToken VerificationToken);
}
