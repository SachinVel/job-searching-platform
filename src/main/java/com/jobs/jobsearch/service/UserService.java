package com.jobs.jobsearch.service;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.VerificationToken;

public interface UserService {

    public static final int MAX_FAILED_ATTEMPTS = 5;

    public static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    public void increaseFailedAttempts(User user);

    public void resetFailedAttempts(String username);

    public void lock(User user);


    public boolean unlockWhenTimeExpired(User user);


    User saveUser(User user) throws UserAlreadyExistException;

    void updateUser(User user);

    User findByUsername(String username);

    User findById(Long userId);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    void deleteVerificationToken(VerificationToken VerificationToken);

    void saveJobSeekerDetails(JobSeekerDetails jobSeekerDetails);

    JobSeekerDetails getJobSeekerDetails(long userId);

    void saveCompanyDetails(Company company);

    Company getCompanyDetails(long userId);
}
