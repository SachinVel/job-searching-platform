package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Job;
import com.jobs.jobsearch.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByJobId(Long jobId);

}
