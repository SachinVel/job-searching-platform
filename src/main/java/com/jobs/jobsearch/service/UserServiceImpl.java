package com.jobs.jobsearch.service;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.repository.CompanyRepository;
import com.jobs.jobsearch.repository.JobSeekerRepository;
import com.jobs.jobsearch.repository.UserRepository;

import com.jobs.jobsearch.repository.VerificationTokenRepository;
import com.jobs.jobsearch.model.VerificationToken;
import com.jobs.jobsearch.util.EscapeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    public static final int MAX_FAILED_ATTEMPTS = 3;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempts(newFailAttempts, user.getUsername());
    }

    @Override
    public void resetFailedAttempts(String username) {
        userRepository.updateFailedAttempts(0, username);
    }

    @Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        userRepository.save(user);
    }

    @Override
    public boolean unlockWhenTimeExpired(User user) {
        Date lockTime = user.getLockTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTime!=null && lockTime.getTime() + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            userRepository.save(user);

            return true;
        }

        return false;
    }

    @Override
    public User saveUser(User user) throws UserAlreadyExistException{
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        return userRepository.save(user);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    private boolean isUsernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public void deleteVerificationToken(VerificationToken verificationToken){
        tokenRepository.delete(verificationToken);
    }

    @Override
    public void saveJobSeekerDetails(JobSeekerDetails jobSeekerDetails) {
        String contactInfo = EscapeUtil.esacpeInput(jobSeekerDetails.getContactInfo());
        jobSeekerDetails.setContactInfo(contactInfo);

        String address = EscapeUtil.esacpeInput(jobSeekerDetails.getAddress());
        jobSeekerDetails.setAddress(address);

        String bio = EscapeUtil.esacpeInput(jobSeekerDetails.getBio());
        jobSeekerDetails.setBio(bio);

        jobSeekerRepository.save(jobSeekerDetails);
    }

    @Override
    public JobSeekerDetails getJobSeekerDetails(long userId){
        return jobSeekerRepository.findByUserId(userId);
    }

    @Override
    public void saveCompanyDetails(Company company) {
        String contactInfo = EscapeUtil.esacpeInput(company.getContactInfo());
        company.setContactInfo(contactInfo);

        String address = EscapeUtil.esacpeInput(company.getAddress());
        company.setAddress(address);

        String companyName = EscapeUtil.esacpeInput(company.getName());
        company.setName(companyName);
        companyRepository.save(company);
    }

    @Override
    public Company getCompanyDetails(long userId) {
        return companyRepository.findByUserId(userId);
    }


}
