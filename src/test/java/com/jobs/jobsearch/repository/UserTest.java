package com.jobs.jobsearch.repository;


import com.jobs.jobsearch.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repo;

    @Test
    @DisplayName("Test create user and persisting in repo")
    public void testCreateUser() {
        User user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setPasswordConfirm("ravi2020");
        user.setUsername("Ravi");

        User savedUser = repo.save(user);

        User existUser = entityManager.find(User.class, savedUser.getId());

        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());

        repo.delete(savedUser);
    }

    @Test
    public void testDeleteUser() {

        User user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setPasswordConfirm("ravi2020");
        user.setUsername("Ravi");

        User savedUser = repo.save(user);

        repo.delete(savedUser);

        User existUser = entityManager.find(User.class, savedUser.getId());

        assertThat(existUser).isEqualTo(null);

    }

    @Test
    public void testFindByUsername(){
        User user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setPasswordConfirm("ravi2020");
        user.setUsername("Ravi");

        User savedUser = repo.save(user);

        User existUser = repo.findByUsername("Ravi");

        assertThat(existUser.getId()).isEqualTo(savedUser.getId());

        repo.delete(savedUser);
    }

    @Test
    public void testFindById(){
        User user = new User();
        user.setEmail("ravikumar@gmail.com");
        user.setPassword("ravi2020");
        user.setPasswordConfirm("ravi2020");
        user.setUsername("Ravi");

        User savedUser = repo.save(user);

        User existUser = repo.findById(savedUser.getId()).get();

        assertThat(existUser.getUsername()).isEqualTo(savedUser.getUsername());

        repo.delete(savedUser);
    }


}
