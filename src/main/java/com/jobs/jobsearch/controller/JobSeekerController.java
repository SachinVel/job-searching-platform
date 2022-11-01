package com.jobs.jobsearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("seeker")
public class JobSeekerController {
    private static final Logger LOGGER= LoggerFactory.getLogger(JobSeekerController.class);

    @GetMapping("/index")
    public String jobseekerhome(){
        return "job-seeker";
    }

}
