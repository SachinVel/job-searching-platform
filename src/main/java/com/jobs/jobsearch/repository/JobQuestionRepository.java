package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.JobQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobQuestionRepository extends JpaRepository<JobQuestion, Long> {

    public List<JobQuestion> findByJobId(Long jobId);
    public void deleteByJobId(Long JobId);
}
