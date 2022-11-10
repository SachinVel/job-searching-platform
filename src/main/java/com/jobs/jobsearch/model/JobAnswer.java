package com.jobs.jobsearch.model;


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
@Table(name="job_answer", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class JobAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(targetEntity = JobApplication.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    JobApplication jobApplication;

    @ManyToOne(targetEntity = JobQuestion.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    JobQuestion jobQuestion;

    String answerValue;

}
