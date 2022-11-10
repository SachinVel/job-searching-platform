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
@Table(name="job", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(targetEntity = Company.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    public Company company;

    @NotEmpty
    public String title ;

    @NotEmpty
    public String description;

    @NotEmpty
    public Long createdTime;

    @NotEmpty
    public Long expiryTime;

    @NotEmpty
    public String Location;

    @NotEmpty
    public Boolean isResumeNeeded;

    @NotEmpty
    public Boolean isCoverLetterNeeded;


}
