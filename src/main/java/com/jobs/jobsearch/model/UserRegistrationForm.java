package com.jobs.jobsearch.model;

import com.jobs.jobsearch.model.helper.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationForm {
    User user;
    JobSeekerDetails jobSeeker;
    Company company;

    public UserRegistrationForm(){
        user = new User();
        user.role = UserRole.JOB_SEEKER;
        jobSeeker = new JobSeekerDetails();
        company = new Company();
    }
}
