package com.jobs.jobsearch.model;

import com.jobs.jobsearch.model.helper.ApplicationStatus;
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
@Table(name="job_application", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(targetEntity = Job.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    Job job;

    @ManyToOne(targetEntity = JobDocument.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "resume_doc_id")
    JobDocument resumeDocument;

    @ManyToOne(targetEntity = JobDocument.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "cover_letter_doc_id")
    JobDocument coverLetterDocument;

    ApplicationStatus applicationStatus;
}
