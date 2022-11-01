package com.jobs.jobsearch.service;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.repository.UserRepository;

import com.jobs.jobsearch.repository.VerificationTokenRepository;
import com.jobs.jobsearch.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User save(User user) throws UserAlreadyExistException{
        if (isUsernameExists(user.getUsername())) {
            throw new UserAlreadyExistException("There is an account with that username: "
                    + user.getUsername());
        }
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


}
