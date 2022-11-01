package com.jobs.jobsearch.security;

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);
}
