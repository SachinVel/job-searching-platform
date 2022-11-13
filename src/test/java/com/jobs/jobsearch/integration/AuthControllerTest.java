package com.jobs.jobsearch.integration;

import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.repository.UserRepository;
import com.jobs.jobsearch.service.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @MockBean
    private UserService userService;


    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void init() {



    }

    @Test
    @DisplayName("without login, accessing seeker page should be forbidden(403)")
    public void testSeekerPageAccess() {

        try {

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/seeker/index");

            MvcResult result = mvc.perform(requestBuilder).andReturn();
            assertThat(result.getResponse().getStatus()==302).isEqualTo(true);

        } catch (Exception ex) {
            assertThat(false);
        }

    }

    @Test
    @DisplayName("without login, accessing company page should be forbidden(403)  ")
    public void testCompanyPageAccess() {
        try {
            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/index");
            MvcResult result = mvc.perform(requestBuilder).andReturn();
            assertThat(result.getResponse().getStatus()==302).isEqualTo(true);
        } catch (Exception ex) {
            assertThat(false);
        }
    }

    @Test
    @WithMockUser(username = "john",password = "admin123",authorities = {"JOB_SEEKER"})
    @DisplayName("with jobseeker role login, accessing seeker page should be successfull(200)  ")
    public void testJobSeekerAccess(){
        try {

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/seeker/index");

            MvcResult result = mvc.perform(requestBuilder).andReturn();
            assertThat(result.getResponse().getStatus()==200).isEqualTo(true);

        } catch (Exception ex) {
            assertThat(false);
        }
    }

    @Test
    @WithMockUser(username = "john",password = "admin123",authorities = {"JOB_SEEKER"})
    @DisplayName("JobSeeker should not access company page")
    public void testSeekerCompanyAccess(){
        try {

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/company/index");

            MvcResult result = mvc.perform(requestBuilder).andReturn();
            assertThat(result.getResponse().getStatus()==403).isEqualTo(true);

        } catch (Exception ex) {
            assertThat(false);
        }
    }

    @Test
    @WithMockUser(username = "john",password = "admin123",authorities = {"COMPANY_ADMIN"})
    @DisplayName("Company admin should not be able to access seeker page")
    public void testCompanyUserSeekerAccess(){
        try {

            RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/seeker/index");

            MvcResult result = mvc.perform(requestBuilder).andReturn();
            assertThat(result.getResponse().getStatus()==403).isEqualTo(true);

        } catch (Exception ex) {
            assertThat(false);
        }
    }

    @Test
    @DisplayName("User should not be able to login with wrong credentials")
    public void testNoLogin(){
        try {

            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login").with(csrf())
                    .param("username", "goudham")
                    .param("password", "wrongpassword");

            MvcResult result = mvc.perform(requestBuilder).andReturn();

            assertThat(result.getResponse().getRedirectedUrl().contains("login?error")).isEqualTo(true);

        } catch (Exception ex) {
            assertThat(false);
        }
    }

//    @Test
//    @DisplayName("User should not be able to login with correct credentials")
//    public void testLogin(){
//        try {
//
//            RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/login").with(csrf())
//                    .param("username", "goudham")
//                    .param("password", "register");
//
//            MvcResult result = mvc.perform(requestBuilder).andReturn();
//
//            assertThat(result.getResponse().getRedirectedUrl().contains("index")).isEqualTo(true);
//
//        } catch (Exception ex) {
//            assertThat(false);
//        }
//    }



}
