package com.jobs.jobsearch.validation;

import com.google.common.collect.Multimap;
import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.service.CompanyService;
import com.jobs.jobsearch.service.JobSeekerService;
import com.jobs.jobsearch.validator.CompanyValidator;
import com.jobs.jobsearch.validator.JobSeekerValidator;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyValidatorTest {

    @Mock
    CompanyService companyService;

    @Mock
    MultiValueMap<String,String> formData;

    @Mock
    Model model;

    @InjectMocks
    CompanyValidator companyValidator;

    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Job should not have large title")
    public void testLargeTitle(){

        Mockito.when(formData.getFirst("title")).thenReturn("SDEasdfasdfasdfasdfadsfasdfSDEasdfasdfasdfasdfadsfasdf");
        Mockito.when(formData.getFirst("description")).thenReturn("some random Description");
        Mockito.when(formData.getFirst("location")).thenReturn("some location");
        Mockito.when(formData.getFirst("expiryTime")).thenReturn("23");
        Mockito.when(formData.getFirst("questions")).thenReturn("");

        boolean isJobDataValid = companyValidator.validateJob(formData,model);

        assertThat(isJobDataValid).isEqualTo(false);
    }

    @Test
    @DisplayName("Job expiry time should not contains alphabets")
    public void testInvallidExpiryTime(){

        Mockito.when(formData.getFirst("title")).thenReturn("SDE");
        Mockito.when(formData.getFirst("description")).thenReturn("some random Description");
        Mockito.when(formData.getFirst("location")).thenReturn("some location");
        Mockito.when(formData.getFirst("expiryTime")).thenReturn("23s");
        Mockito.when(formData.getFirst("questions")).thenReturn("");

        boolean isJobDataValid = companyValidator.validateJob(formData,model);

        assertThat(isJobDataValid).isEqualTo(false);
    }

    @Test
    @DisplayName("Job Location should not be large")
    public void testInvallidLocation(){

        Mockito.when(formData.getFirst("title")).thenReturn("SDE");
        Mockito.when(formData.getFirst("description")).thenReturn("some random Description");
        Mockito.when(formData.getFirst("location")).thenReturn("some locationsome locationsome locationsome locationsome locationsome locationsome location");
        Mockito.when(formData.getFirst("expiryTime")).thenReturn("23s");
        Mockito.when(formData.getFirst("questions")).thenReturn("");

        boolean isJobDataValid = companyValidator.validateJob(formData,model);

        assertThat(isJobDataValid).isEqualTo(false);
    }
}
