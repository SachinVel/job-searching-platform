package com.jobs.jobsearch.repository;


import com.jobs.jobsearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.username = ?2")
    @Modifying
    @Transactional
    public void updateFailedAttempts(int failAttempts, String username);

    User findByUsername(String username);

}