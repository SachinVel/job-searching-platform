package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.JobDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobDocumentRepository extends JpaRepository<JobDocument, Long> {

    List<JobDocument> findByUserId(Long userId);
}
