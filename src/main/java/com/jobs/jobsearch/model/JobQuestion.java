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
@Table(name="job_question", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})

public class JobQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(targetEntity = Job.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    public Job job;

    @NotEmpty
    private String questionName;

}
