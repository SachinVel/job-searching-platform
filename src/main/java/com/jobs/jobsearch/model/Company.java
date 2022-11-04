package com.jobs.jobsearch.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="company", uniqueConstraints = {@UniqueConstraint(columnNames = {"companyId"})})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long companyId;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER,cascade=CascadeType.REMOVE)
    @JoinColumn(nullable = false, name = "admin_id")
    private User user;

    @NotEmpty
    private String name;

    @NotEmpty
    private String address;

    @NotEmpty
    private String contactInfo;

}
