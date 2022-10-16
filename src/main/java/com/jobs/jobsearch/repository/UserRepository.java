package com.jobs.jobsearch.repository;


import com.jobs.jobsearch.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {


}