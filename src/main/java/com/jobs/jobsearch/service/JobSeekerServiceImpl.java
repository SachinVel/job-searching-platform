package com.jobs.jobsearch.service;

import com.jobs.jobsearch.model.JobAnswer;
import com.jobs.jobsearch.model.JobApplication;
import com.jobs.jobsearch.model.JobDocument;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.helper.DocType;
import com.jobs.jobsearch.repository.JobAnswerRepository;
import com.jobs.jobsearch.repository.JobApplicationRepository;
import com.jobs.jobsearch.repository.JobDocumentRepository;
import com.jobs.jobsearch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerServiceImpl implements  JobSeekerService{

    @Autowired
    private JobDocumentRepository jobDocumentRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobAnswerRepository jobAnswerRepository;

    @Override
    public JobApplication saveApplication(JobApplication jobApplication) {
        return jobApplicationRepository.save(jobApplication);
    }

    @Override
    public List<JobApplication> getUserJobApplications(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }

    @Override
    public void saveJobAnswers(List<JobAnswer> jobAnswers) {
        for( JobAnswer jobAnswer : jobAnswers ){
            String encodedAnswer = jobAnswer.getAnswerValue();
            jobAnswer.setAnswerValue(encodedAnswer);
        }
        jobAnswerRepository.saveAll(jobAnswers);
    }

    @Override
    public void saveDocument(JobDocument jobDocument){
        jobDocumentRepository.save(jobDocument);
    }

    @Override
    public List<JobDocument> getUserDocuments(Long userId) {
        return jobDocumentRepository.findByUserId(userId);
    }

    @Override
    public JobDocument getDocument(Long docId) {
        return jobDocumentRepository.getReferenceById(docId);
    }

    @Override
    public void deleteDocument(Long docId) {
        jobDocumentRepository.deleteById(docId);
    }
}
