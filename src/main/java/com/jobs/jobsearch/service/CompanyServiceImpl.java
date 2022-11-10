package com.jobs.jobsearch.service;

import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements  CompanyService{

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobQuestionRepository jobQuestionRepository;

    @Override
    public Company getCompanyByUserId(Long userId) {
        return companyRepository.findByUserId(userId);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public List<Job> getCompanyJobs(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    @Override
    public void deleteJob(Long jobId) {
        for( JobQuestion question : getJobQuestions(jobId) ){
            jobQuestionRepository.deleteById(question.getId());
        }
        jobRepository.deleteById(jobId);
    }

    @Override
    public List<JobQuestion> getJobQuestions(Long jobId){
        return jobQuestionRepository.findByJobId(jobId);
    }

    @Override
    public JobQuestion getJobQuestionById(Long questionId) {
        return jobQuestionRepository.getReferenceById(questionId);
    }

    @Override
    public void deleteJobQuestions(Long jobId) {
        for( JobQuestion question : getJobQuestions(jobId) ){
            jobQuestionRepository.deleteById(question.getId());
        }
    }

    @Override
    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job getJob(long jobId) {
        return jobRepository.getReferenceById(jobId);
    }

    @Override
    public void saveJobQuestions(List<JobQuestion> jobQuestions) {
        jobQuestionRepository.saveAll(jobQuestions);
    }

    @Override
    public List<JobApplication> getJobApplicationByJobId(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    @Override
    public List<JobSeekerDetails> getAllJobSeekers() {
        return jobSeekerRepository.findAll();
    }
}
