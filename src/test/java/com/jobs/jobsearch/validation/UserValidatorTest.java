package com.jobs.jobsearch.validation;

import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.UserRegistrationForm;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.service.UserService;
import com.jobs.jobsearch.validator.UserValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserValidatorTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserValidator userValidator;

    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("UserValidator should return error when username has non alphanumeric character")
    public void testUserNameValidation(){

        Mockito.when(userService.findByUsername("goudham")).thenReturn(null);

        UserRegistrationForm form = new UserRegistrationForm();
        User user = new User();
        user.setUsername("asdadsf-sdf");
        user.setPassword("asdfsadf");
        user.setPasswordConfirm("asdfsadf");
        user.setRole(UserRole.JOB_SEEKER);
        user.setEmail("vsdaf@gmail.com");
        form.setUser(user);

        JobSeekerDetails jobSeeker = new JobSeekerDetails();
        jobSeeker.setUser(user);
        jobSeeker.setBio("sdfsddsffsd");
        jobSeeker.setContactInfo("24-089-0987");
        jobSeeker.setAddress("sdfsddsffsd");
        form.setJobSeeker(jobSeeker);


        Errors errors = new BeanPropertyBindingResult(form, "");
        userValidator.validateRegistrationForm(form, errors);

        assertThat(errors.getFieldError("user.username").toString().contains("nonAlphaNumeric")).isEqualTo(true);
    }

    @Test
    @DisplayName("UserValidator should return error when username already exists")
    public void testUserNameExistsValidation(){



        UserRegistrationForm form = new UserRegistrationForm();
        User user = new User();
        user.setUsername("john1234");
        user.setPassword("asdfsadf");
        user.setPasswordConfirm("asdfsadf");
        user.setRole(UserRole.JOB_SEEKER);
        user.setEmail("dummymail@gmail.com");
        form.setUser(user);

        Mockito.when(userService.findByUsername("john1234")).thenReturn(user);

        JobSeekerDetails jobSeeker = new JobSeekerDetails();
        jobSeeker.setUser(user);
        jobSeeker.setBio("bio desc");
        jobSeeker.setContactInfo("240-089-0987");
        jobSeeker.setAddress("address lines");
        form.setJobSeeker(jobSeeker);


        Errors errors = new BeanPropertyBindingResult(form, "");
        userValidator.validateRegistrationForm(form, errors);

        System.out.println(errors.getFieldError("user.username"));
        assertThat(errors.getFieldError("user.username").toString().contains("Duplicate.userForm.username")).isEqualTo(true);
    }

    @Test
    @DisplayName("UserValidator should return error when password is not matched")
    public void testPasswordValidation(){

        UserRegistrationForm form = new UserRegistrationForm();
        User user = new User();
        user.setUsername("john");
        user.setPassword("Aa*122222");
        user.setPasswordConfirm("Bb*122222");//different password
        user.setRole(UserRole.JOB_SEEKER);
        user.setEmail("vsdaf@gmail.com");
        form.setUser(user);

        Mockito.when(userService.findByUsername("asdadsf")).thenReturn(user);

        JobSeekerDetails jobSeeker = new JobSeekerDetails();
        jobSeeker.setUser(user);
        jobSeeker.setBio("sdfsddsffsd");
        jobSeeker.setContactInfo("24-089-0987");
        jobSeeker.setAddress("sdfsddsffsd");
        form.setJobSeeker(jobSeeker);


        Errors errors = new BeanPropertyBindingResult(form, "");
        userValidator.validateRegistrationForm(form, errors);

        System.out.println(errors.getFieldError("user.passwordConfirm"));
        assertThat(errors.getFieldError("user.passwordConfirm").toString().contains("Diff.userForm.passwordConfirm")).isEqualTo(true);
    }


    //To-do check all other user validations

}
