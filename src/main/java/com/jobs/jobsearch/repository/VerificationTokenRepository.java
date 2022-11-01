package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}

