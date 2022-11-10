package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.JobAnswer;
import com.jobs.jobsearch.model.JobQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAnswerRepository extends JpaRepository<JobAnswer, Long> {
}
