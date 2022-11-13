package com.jobs.jobsearch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class JobsearchApplication {

	private static final Logger LOGGER= LoggerFactory.getLogger(JobsearchApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(JobsearchApplication.class, args);
	}

}