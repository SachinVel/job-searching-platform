package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.JobSeekerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerRepository extends JpaRepository<JobSeekerDetails, Long> {

    JobSeekerDetails findByUserId(Long userId);

}
