package com.jobs.jobsearch.service;

import com.jobs.jobsearch.model.JobAnswer;
import com.jobs.jobsearch.model.JobApplication;
import com.jobs.jobsearch.model.JobDocument;
import com.jobs.jobsearch.model.User;

import java.util.List;

public interface JobSeekerService {

    JobApplication saveApplication(JobApplication jobApplication);

    List<JobApplication> getUserJobApplications(Long userId);

    JobApplication getApplication(Long appId);

    void deleteJobApplication(Long applicationId);

    void saveJobAnswers(List<JobAnswer> jobAnswers);

    void saveDocument(JobDocument jobDocument);
    List<JobDocument> getUserDocuments(Long userId);

    JobDocument getDocument(Long docId);

    void deleteDocument(Long docId);
}
