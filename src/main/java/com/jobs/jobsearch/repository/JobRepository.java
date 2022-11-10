package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Job;
import com.jobs.jobsearch.model.JobDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyId(Long companyId);
}
