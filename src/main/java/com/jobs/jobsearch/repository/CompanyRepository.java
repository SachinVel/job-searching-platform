package com.jobs.jobsearch.repository;

import com.jobs.jobsearch.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByUserId(Long userId);
}
