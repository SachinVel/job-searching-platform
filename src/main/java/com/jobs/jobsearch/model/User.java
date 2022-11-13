package com.jobs.jobsearch.model;

import com.jobs.jobsearch.model.helper.UserRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @NotEmpty
    public String username;
    public boolean enabled;

    @NotEmpty
    public UserRole role;
    @NotEmpty
    public String email;
    @NotEmpty
    public String password;

    @Transient
    @NotEmpty
    public String passwordConfirm;


    private boolean accountNonLocked;

    private int failedAttempt;

    private Date lockTime;

    public User() {
        super();
        this.enabled=false;
        this.accountNonLocked = true;
    }


}

