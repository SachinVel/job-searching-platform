package com.jobs.jobsearch.service;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.VerificationToken;

public interface UserService {
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
