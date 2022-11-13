package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.DocType;
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
public class JobDocumentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobDocumentRepository jobDocumentRepository;

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
    @DisplayName("Test create job document functionality")
    public void testCreateJobDocument() {
        JobDocument jobDocument = new JobDocument();
        jobDocument.setName("testfile.pdf");
        jobDocument.setType(DocType.RESUME);
        jobDocument.setUser(user);

        JobDocument jobDoc = jobDocumentRepository.save(jobDocument);

        JobDocument persistedDoc = entityManager.find(JobDocument.class, jobDoc.getDocId());

        assertThat(persistedDoc.getName()).isEqualTo(jobDoc.getName());

        jobDocumentRepository.delete(jobDoc);
    }

    @Test
    @DisplayName("check getDocumentByUserId method")
    public void testGetDocumentByUserId() {
        JobDocument jobDocument = new JobDocument();
        jobDocument.setName("testfile.pdf");
        jobDocument.setType(DocType.RESUME);
        jobDocument.setUser(user);

        JobDocument jobDoc = jobDocumentRepository.save(jobDocument);

        JobDocument persistedDoc = jobDocumentRepository.findByUserId(user.getId()).get(0);

        assertThat(persistedDoc.getName()).isEqualTo(jobDoc.getName());

        jobDocumentRepository.delete(jobDoc);
    }

    @AfterAll
    public void destroy(){
        userRepository.delete(user);
    }
}
