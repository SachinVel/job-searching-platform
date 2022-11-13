package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.ApplicationStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobAnswerTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobQuestionRepository jobQuestionRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobAnswerRepository jobAnswerRepository;


    User user;

    Company company;

    Job job;

    JobApplication jobApplication;

    JobQuestion jobQuestion;



    @BeforeAll
    public void init(){
        user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setPasswordConfirm("ravi2020");
        user.setUsername("Ravi");
        userRepository.save(user);

        company = new Company();
        company.setUser(user);
        company.setName("UMD");
        company.setAddress("Maryland");
        company.setContactInfo("240-543-9876");
        companyRepository.save(company);

        job = new Job();
        job.setLocation("Maryland");
        job.setDescription("Software developer role");
        job.setTitle("SDE");
        job.setCompany(company);
        job.setExpiryTime(System.currentTimeMillis()+1000);
        job.setCreatedTime(System.currentTimeMillis());
        job.setIsCoverLetterNeeded(false);
        job.setIsResumeNeeded(false);

        jobRepository.save(job);

        jobApplication = new JobApplication();
        jobApplication.setApplicationStatus(ApplicationStatus.ACCEPTED);
        jobApplication.setJob(job);
        jobApplication.setUser(user);
        jobApplication.setResumeDocument(null);
        jobApplication.setCoverLetterDocument(null);

        jobApplicationRepository.save(jobApplication);

        jobQuestion = new JobQuestion();
        jobQuestion.setQuestionName("What is your expected salary?");
        jobQuestion.setJob(job);

        jobQuestionRepository.save(jobQuestion);

    }

    @Test
    @DisplayName("check creating application answers ")
    public void testCreateJobAnswer() {
        JobAnswer jobAnswer = new JobAnswer();
        jobAnswer.setJobApplication(jobApplication);
        jobAnswer.setAnswerValue("50,000 USD");
        jobAnswer.setJobQuestion(jobQuestion);

        jobAnswer = jobAnswerRepository.save(jobAnswer);
        JobAnswer persistedAnswer = entityManager.find(JobAnswer.class, jobAnswer.getId());

        assertThat(persistedAnswer.getId()).isEqualTo(jobAnswer.getId());

        jobAnswerRepository.delete(jobAnswer);
    }

    @Test
    @DisplayName("check getting application answers using applicationId")
    public void testGetApplicationByUserId() {
        JobAnswer jobAnswer = new JobAnswer();
        jobAnswer.setJobApplication(jobApplication);
        jobAnswer.setAnswerValue("50,000 USD");
        jobAnswer.setJobQuestion(jobQuestion);

        jobAnswer = jobAnswerRepository.save(jobAnswer);
        JobAnswer persistedAnswer = jobAnswerRepository.findByJobApplicationId(jobApplication.getId()).get(0);

        assertThat(persistedAnswer.getId()).isEqualTo(jobAnswer.getId());

        jobAnswerRepository.delete(jobAnswer);
    }



    @AfterAll
    public void destroy(){
        jobQuestionRepository.delete(jobQuestion);
        jobApplicationRepository.delete(jobApplication);
        jobRepository.delete(job);
        companyRepository.delete(company);
        userRepository.delete(user);
    }


}
