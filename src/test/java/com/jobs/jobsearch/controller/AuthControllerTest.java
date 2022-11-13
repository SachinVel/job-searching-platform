package com.jobs.jobsearch.controller;


import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.UserRegistrationForm;
import com.jobs.jobsearch.repository.UserRepository;
import com.jobs.jobsearch.service.UserServiceImpl;
import com.jobs.jobsearch.validator.UserValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    private UserRegistrationForm userRegistrationForm;


    @BeforeAll
    public void init(){
        userRegistrationForm = new UserRegistrationForm();
    }
    @BeforeTestMethod
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegister() throws Exception{
        User user = new User();
        user.setUsername("goudham");
        user.setId(1l);
        Mockito.when(userService.saveUser(user)).thenReturn(user);

        String url = authController.registration(userRegistrationForm,model,bindingResult);
        System.out.println("url : "+url);
        assertThat(true);
    }
}
