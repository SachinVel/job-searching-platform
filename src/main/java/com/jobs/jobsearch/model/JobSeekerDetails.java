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
@Table(name = "job_seeker_details")
public class JobSeekerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "user_id")
    User user;

    @NotEmpty
    private String bio;
    @NotEmpty
    private String address;
    @NotEmpty
    private String contactInfo;

}
