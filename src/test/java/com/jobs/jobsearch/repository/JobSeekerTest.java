package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobQuestion;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import org.aspectj.lang.annotation.After;
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
public class JobSeekerTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;


    User user;

    @BeforeAll
    public void init(){
        user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setPasswordConfirm("ravi2020");
        user.setUsername("Ravi");
        userRepository.save(user);
    }

    @Test
    @DisplayName("check whether the job seeker data is created ")
    public void testCreateJobSeeker() {
        JobSeekerDetails jobSeekerDetails = new JobSeekerDetails();

        jobSeekerDetails.setBio("I'm 24 years old.");
        jobSeekerDetails.setAddress("College Park, Maryland");
        jobSeekerDetails.setContactInfo("1203-43534-34");

        jobSeekerDetails = jobSeekerRepository.save(jobSeekerDetails);

        JobSeekerDetails persistedSeekerDetails = entityManager.find(JobSeekerDetails.class, jobSeekerDetails.getSeekerId());

        assertThat(persistedSeekerDetails.getSeekerId()).isEqualTo(jobSeekerDetails.getSeekerId());

        jobSeekerRepository.delete(jobSeekerDetails);
    }

    @Test
    @DisplayName("check getting job seeker details using userId")
    public void testGetJobSeekerByUserId() {
        JobSeekerDetails jobSeekerDetails = new JobSeekerDetails();

        jobSeekerDetails.setBio("I'm 24 years old.");
        jobSeekerDetails.setAddress("College Park, Maryland");
        jobSeekerDetails.setContactInfo("1203-43534-34");
        jobSeekerDetails.setUser(user);

        jobSeekerDetails = jobSeekerRepository.save(jobSeekerDetails);

        JobSeekerDetails persistedSeekerDetails = jobSeekerRepository.findByUserId(user.getId());

        assertThat(persistedSeekerDetails.getSeekerId()).isEqualTo(jobSeekerDetails.getSeekerId());

        jobSeekerRepository.delete(jobSeekerDetails);
    }

    @AfterAll
    public void destroy(){
        userRepository.delete(user);
    }
}
