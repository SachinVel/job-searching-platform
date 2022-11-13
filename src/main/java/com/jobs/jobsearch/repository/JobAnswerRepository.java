package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.JobAnswer;
import com.jobs.jobsearch.model.JobQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobAnswerRepository extends JpaRepository<JobAnswer, Long> {
    public List<JobAnswer> findByJobApplicationId(Long applicationId);
    public List<JobAnswer> findByJobQuestionId(Long questionId);
}
