package com.jobs.jobsearch.validation;

import com.jobs.jobsearch.model.JobDocument;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.UserRegistrationForm;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.service.JobSeekerService;
import com.jobs.jobsearch.service.UserService;
import com.jobs.jobsearch.validator.JobSeekerValidator;
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
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobSeekerValidatorTest {
    @Mock
    JobSeekerService jobSeekerService;

    @Mock
    Model model;

    @Mock
    MultipartFile file;

    @InjectMocks
    JobSeekerValidator seekerValidator;

    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("JobSeekerDocument should not allow files other than pdf and jpeg")
    public void testOtherFileTypes(){


        UserRegistrationForm form = new UserRegistrationForm();
        User user = new User();
        user.setUsername("asdadsf-sdf");
        user.setPassword("asdfsadf");
        user.setPasswordConfirm("asdfsadf");
        user.setRole(UserRole.JOB_SEEKER);
        user.setEmail("vsdaf@gmail.com");
        form.setUser(user);

        Mockito.when(jobSeekerService.getUserDocuments(user.getId())).thenReturn(new ArrayList<JobDocument>());

        Mockito.when(file.getOriginalFilename()).thenReturn("some-file-name.jpeg");

        boolean isDocumentValidated = seekerValidator.validateDocument(user,file,model);

        assertThat(isDocumentValidated).isEqualTo(false);
    }

    @Test
    @DisplayName("Allow pdf files")
    public void testPDFFile(){

        UserRegistrationForm form = new UserRegistrationForm();
        User user = new User();
        user.setUsername("asdadsf-sdf");
        user.setPassword("asdfsadf");
        user.setPasswordConfirm("asdfsadf");
        user.setRole(UserRole.JOB_SEEKER);
        user.setEmail("vsdaf@gmail.com");
        form.setUser(user);

        Mockito.when(jobSeekerService.getUserDocuments(user.getId())).thenReturn(new ArrayList<JobDocument>());

        Mockito.when(file.getOriginalFilename()).thenReturn("some-file-name.pdf");

        boolean isDocumentValidated = seekerValidator.validateDocument(user,file,model);

        assertThat(isDocumentValidated).isEqualTo(true);
    }


}
