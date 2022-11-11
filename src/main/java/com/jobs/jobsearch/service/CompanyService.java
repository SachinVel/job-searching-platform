package com.jobs.jobsearch.service;

import com.jobs.jobsearch.model.*;

import java.util.List;

public interface CompanyService {

    Company getCompanyByUserId(Long userId);

    List<Job> getAllJobs();

    List<Job> getCompanyJobs(Long companyId);

    void deleteJob(Long jobId);

    Job saveJob(Job job);

    Job getJob(long jobId);

    public List<JobQuestion> getJobQuestions(Long jobId);

    public JobQuestion getJobQuestionById(Long questionId);

    public void deleteJobQuestions(Long jobId);

    void saveJobQuestions(List<JobQuestion> jobQuestions);

    List<JobApplication> getJobApplicationByJobId(Long jobId);

    JobApplication getJobApplicationById(Long appId);

    List<JobAnswer> getAnswersByApplicationId(Long applicationId);

    List<JobSeekerDetails> getAllJobSeekers();
}
