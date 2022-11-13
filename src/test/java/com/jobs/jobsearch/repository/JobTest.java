package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.Job;
import com.jobs.jobsearch.model.User;
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
public class JobTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    User user;

    Company company;

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

    }

    @Test
    @DisplayName("Test create job functionality")
    public void testCreateJob() {
        Job job = new Job();
        job.setLocation("Maryland");
        job.setDescription("Software developer role");
        job.setTitle("SDE");
        job.setCompany(company);
        job.setExpiryTime(System.currentTimeMillis()+1000);
        job.setCreatedTime(System.currentTimeMillis());
        job.setIsCoverLetterNeeded(false);
        job.setIsResumeNeeded(false);

        Job persistedJob = jobRepository.save(job);

        Job existUser = entityManager.find(Job.class, persistedJob.getId());

        assertThat(existUser.getTitle()).isEqualTo(persistedJob.getTitle());

        jobRepository.delete(job);
    }

    @Test
    @DisplayName("Test create job functionality")
    public void testGetJobByCompany() {
        Job job = new Job();
        job.setLocation("Maryland");
        job.setDescription("Software developer role");
        job.setTitle("SDE");
        job.setCompany(company);
        job.setExpiryTime(System.currentTimeMillis()+1000);
        job.setCreatedTime(System.currentTimeMillis());
        job.setIsCoverLetterNeeded(false);
        job.setIsResumeNeeded(false);

        Job persistedJob = jobRepository.save(job);

        Job companyJob = jobRepository.findByCompanyId(company.getId()).get(0);

        assertThat(companyJob.getTitle()).isEqualTo(persistedJob.getTitle());

        jobRepository.delete(job);
    }


    @AfterAll
    public void destroy(){
        companyRepository.delete(company);
        userRepository.delete(user);
    }


}
