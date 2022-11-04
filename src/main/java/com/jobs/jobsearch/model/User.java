package com.jobs.jobsearch.model;

import com.jobs.jobsearch.model.helper.UserRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="users", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @NotEmpty
    private String username;
    private boolean enabled;

    @NotEmpty
    private UserRole role;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

    @Transient
    @NotEmpty
    private String passwordConfirm;

    public User() {
        super();
        this.enabled=false;
    }


}

