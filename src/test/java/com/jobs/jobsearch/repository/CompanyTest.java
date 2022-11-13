package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobSeekerDetails;
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
public class CompanyTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;


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
    @DisplayName("check whether the company data is created ")
    public void testCreateCompany() {
        Company company = new Company();

        company.setName("Paypal");
        company.setAddress("College Park, Maryland");
        company.setContactInfo("1203-43534-34");

        company = companyRepository.save(company);

        Company persistedCompany = entityManager.find(Company.class, company.getId());

        assertThat(persistedCompany.getId()).isEqualTo(company.getId());

        companyRepository.delete(company);
    }

    @Test
    @DisplayName("check getting job seeker details using userId")
    public void testGetJobSeekerByUserId() {
        Company company = new Company();

        company.setName("Paypal");
        company.setAddress("College Park, Maryland");
        company.setContactInfo("1203-43534-34");
        company.setUser(user);

        company = companyRepository.save(company);

        Company persistedCompany = companyRepository.findByUserId(user.getId());

        assertThat(persistedCompany.getId()).isEqualTo(company.getId());

        companyRepository.delete(company);
    }

    @AfterAll
    public void destroy(){
        userRepository.delete(user);
    }
}
