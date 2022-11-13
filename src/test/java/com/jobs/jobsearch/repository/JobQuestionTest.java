package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.Job;
import com.jobs.jobsearch.model.JobQuestion;
import com.jobs.jobsearch.model.User;
import org.checkerframework.checker.units.qual.C;
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
public class JobQuestionTest {
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
    @DisplayName("Test create job Question functionality")
    public void testCreateJobQuestion() {
        JobQuestion jobQuestion = new JobQuestion();
        jobQuestion.setQuestionName("What is your expected salary?");
        jobQuestion.setJob(job);

        JobQuestion jobQues = jobQuestionRepository.save(jobQuestion);

        JobQuestion persistedQuestion = entityManager.find(JobQuestion.class, jobQues.getId());

        assertThat(persistedQuestion.getQuestionName()).isEqualTo(jobQues.getQuestionName());

        jobQuestionRepository.delete(jobQues);
    }

    @Test
    @DisplayName("check getting question based on job id")
    public void testGetJobQuestionByJobId() {
        JobQuestion jobQuestion = new JobQuestion();
        jobQuestion.setQuestionName("What is your expected salary?");
        jobQuestion.setJob(job);

        JobQuestion jobQues = jobQuestionRepository.save(jobQuestion);

        JobQuestion persistedQues = jobQuestionRepository.findByJobId(job.getId()).get(0);

        assertThat(persistedQues.getQuestionName()).isEqualTo(jobQues.getQuestionName());

        jobQuestionRepository.delete(jobQues);
    }




    @AfterAll
    public void destroy(){
        jobRepository.delete(job);
        companyRepository.delete(company);
        userRepository.delete(user);
    }


}
