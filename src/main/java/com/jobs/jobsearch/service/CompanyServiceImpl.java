package com.jobs.jobsearch.service;

import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.repository.*;
import com.jobs.jobsearch.util.EscapeUtil;
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

    @Autowired
    private JobAnswerRepository jobAnswerRepository;

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

        for( JobApplication jobApplication : getJobApplicationByJobId(jobId) ){
            for( JobAnswer jobAnswer : getAnswersByApplicationId(jobApplication.getId()) ){
                jobAnswerRepository.delete(jobAnswer);
            }
            jobApplicationRepository.delete(jobApplication);
        }
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
            List<JobAnswer> jobAnswers = jobAnswerRepository.findByJobQuestionId(question.getId());
            for (JobAnswer jobAnswer : jobAnswers ){
                jobAnswerRepository.deleteById(jobAnswer.getId());
            }
            jobQuestionRepository.deleteById(question.getId());
        }
    }

    @Override
    public Job saveJob(Job job) {
        String encodedStr = job.getTitle();
        encodedStr = EscapeUtil.esacpeInput(encodedStr);
        job.setTitle(encodedStr);

        encodedStr = job.getDescription();
        encodedStr = EscapeUtil.esacpeInput(encodedStr);
        job.setDescription(encodedStr);

        encodedStr = job.getLocation();
        encodedStr = EscapeUtil.esacpeInput(encodedStr);
        job.setLocation(encodedStr);

        return jobRepository.save(job);
    }

    @Override
    public Job getJob(long jobId) {
        return jobRepository.getReferenceById(jobId);
    }

    @Override
    public void saveJobQuestions(List<JobQuestion> jobQuestions) {

        for( JobQuestion jobQuestion : jobQuestions ){
            String encodedStr = jobQuestion.getQuestionName();
            encodedStr = EscapeUtil.esacpeInput(encodedStr);
            jobQuestion.setQuestionName(encodedStr);
        }

        jobQuestionRepository.saveAll(jobQuestions);
    }

    @Override
    public List<JobApplication> getJobApplicationByJobId(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    @Override
    public JobApplication getJobApplicationById(Long appId) {
        return jobApplicationRepository.getReferenceById(appId);
    }

    @Override
    public List<JobAnswer> getAnswersByApplicationId(Long applicationId) {
        return jobAnswerRepository.findByJobApplicationId(applicationId);
    }

    @Override
    public List<JobSeekerDetails> getAllJobSeekers() {
        return jobSeekerRepository.findAll();
    }
}
