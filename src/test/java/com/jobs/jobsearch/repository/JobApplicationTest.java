package com.jobs.jobsearch.repository;


import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.ApplicationStatus;
import com.jobs.jobsearch.model.helper.DocType;
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
public class JobApplicationTest {

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

    User user;

    Company company;

    Job job;

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

    }

    @Test
    @DisplayName("Test create job application functionality")
    public void testCreateJobApplication() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setApplicationStatus(ApplicationStatus.ACCEPTED);
        jobApplication.setJob(job);
        jobApplication.setUser(user);
        jobApplication.setResumeDocument(null);
        jobApplication.setCoverLetterDocument(null);

        jobApplicationRepository.save(jobApplication);

        JobApplication persistedApplication = entityManager.find(JobApplication.class, jobApplication.getId());

        assertThat(persistedApplication.getId()).isEqualTo(jobApplication.getId());

        jobApplicationRepository.delete(jobApplication);
    }

    @Test
    @DisplayName("check get job applications of a specific job")
    public void testGetApplicationByJobId() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setApplicationStatus(ApplicationStatus.ACCEPTED);
        jobApplication.setJob(job);
        jobApplication.setUser(user);
        jobApplication.setResumeDocument(null);
        jobApplication.setCoverLetterDocument(null);

        jobApplicationRepository.save(jobApplication);

        JobApplication persistedApplication = jobApplicationRepository.findByJobId(job.getId()).get(0);

        assertThat(persistedApplication.getId()).isEqualTo(jobApplication.getId());

        jobApplicationRepository.delete(jobApplication);
    }

    @Test
    @DisplayName("check get job applications of a specific user")
    public void testGetApplicationByUserId() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setApplicationStatus(ApplicationStatus.ACCEPTED);
        jobApplication.setJob(job);
        jobApplication.setUser(user);
        jobApplication.setResumeDocument(null);
        jobApplication.setCoverLetterDocument(null);

        jobApplicationRepository.save(jobApplication);

        JobApplication persistedApplication = jobApplicationRepository.findByUserId(user.getId()).get(0);

        assertThat(persistedApplication.getId()).isEqualTo(jobApplication.getId());

        jobApplicationRepository.delete(jobApplication);
    }

    @AfterAll
    public void destroy(){
        jobRepository.delete(job);
        companyRepository.delete(company);
        userRepository.delete(user);
    }
}
