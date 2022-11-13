package com.jobs.jobsearch.service;

import com.jobs.jobsearch.repository.UserRepository;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobSeekerServiceTest {

    @InjectMocks
    private JobSeekerService testingObject;

    @Mock
    private UserRepository userRepository;

    @BeforeTestMethod
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
}
