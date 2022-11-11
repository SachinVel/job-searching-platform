package com.jobs.jobsearch.controller;

import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.ApplicationStatus;
import com.jobs.jobsearch.service.CompanyService;
import com.jobs.jobsearch.service.JobSeekerService;
import com.jobs.jobsearch.service.UserService;
import com.jobs.jobsearch.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("company")
public class CompanyAdminController {

    private static final Logger LOGGER= LoggerFactory.getLogger(CompanyAdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobSeekerService jobSeekerService;

    @Autowired
    private UserValidator userValidator;

    @Value("${document.location}")
    private String documentLocation;

    @GetMapping("/index")
    public String companyAdminHome(){
        return "company-admin";
    }

    @GetMapping("/job")
    public String getJob(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Company company = companyService.getCompanyByUserId(user.getId());
        List<Job> jobs = companyService.getCompanyJobs(company.getId());
        model.addAttribute("jobs",jobs);
        return "company-job";
    }

    @GetMapping("/job/{jobId}")
    public String getJobDetails(@PathVariable("jobId")Long jobId, Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Company company = companyService.getCompanyByUserId(user.getId());
        Job job = companyService.getJob(jobId);
        List<JobQuestion> jobQuestions = companyService.getJobQuestions(jobId);
        Long expiryTimestamp = job.getExpiryTime();
        double expiryInDays = (int)(expiryTimestamp/(24*60*60*1000))-(System.currentTimeMillis()/(24*60*60*1000));
        long days = (int)Math.ceil(expiryInDays);
        job.setExpiryTime(days);
        model.addAttribute("job",job);
        model.addAttribute("questions",jobQuestions);
        return "company-add-job";

    }

    @GetMapping("/job/application/{jobId}")
    public String getJobApplications(@PathVariable("jobId")Long jobId, Model model){
        List<JobApplication> jobApplications = companyService.getJobApplicationByJobId(jobId);
        List<JobSeekerDetails> jobSeekers = companyService.getAllJobSeekers();
        model.addAttribute("jobApplications", jobApplications);
        model.addAttribute("jobSeekers", jobSeekers);
        return "company-job-application";

    }

    @GetMapping("/application/{appId}")
    public String getJobApplication(@PathVariable("appId")Long applicationId, Model model){

        JobApplication jobApplication = companyService.getJobApplicationById(applicationId);

        JobSeekerDetails jobSeekerDetails = userService.getJobSeekerDetails(jobApplication.getUser().getId());

        List<JobAnswer> jobAnswers = companyService.getAnswersByApplicationId(applicationId);

        model.addAttribute("jobSeeker", jobSeekerDetails);
        model.addAttribute("jobApplication", jobApplication);
        model.addAttribute("jobAnswers", jobAnswers);
        return "company-application-detail";
    }

    @PostMapping(
            path = "/application",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public String updateJobApplication(@RequestBody MultiValueMap<String, String> formData, Model model){

        Long applicationId = Long.parseLong(formData.getFirst("appId"));
        JobApplication jobApplication = companyService.getJobApplicationById(applicationId);
        String status = formData.getFirst("status");
        switch (status){
            case "accept":
                jobApplication.setApplicationStatus(ApplicationStatus.ACCEPTED);
                break;
            case "reject":
                jobApplication.setApplicationStatus(ApplicationStatus.REJECTED);
                break;
            case "waitlist":
                jobApplication.setApplicationStatus(ApplicationStatus.WAILISTED);
                break;
        }

        jobSeekerService.saveApplication(jobApplication);

        JobSeekerDetails jobSeekerDetails = userService.getJobSeekerDetails(jobApplication.getUser().getId());

        List<JobAnswer> jobAnswers = companyService.getAnswersByApplicationId(applicationId);


        model.addAttribute("jobSeeker", jobSeekerDetails);
        model.addAttribute("jobApplication", jobApplication);
        model.addAttribute("jobAnswers", jobAnswers);
        model.addAttribute("message", "Status updated successfully");
        return "company-application-detail";
    }

    @GetMapping("/document/download/{docId}")
    public ResponseEntity<?> downloadFile(@PathVariable("docId") Long docId) {

        try{

            JobDocument curDoc = jobSeekerService.getDocument(docId);

            String docLocation = documentLocation+"/"+curDoc.getUser().getId()+"/"+curDoc.getName();
            Resource resource = new FileUrlResource(docLocation);
            if (resource == null) {
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }

            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        }catch (Exception e) {
            LOGGER.error("Exception in downloading file : {}",e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/job/add")
    public String showJobForm(Model model){
        model.addAttribute("job", new Job());
        return "company-add-job";
    }

    @PostMapping(
            path = "/job/update",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public String addJob(@RequestBody MultiValueMap<String, String> formData, Model model){
        for( String key : formData.keySet() ){
            String value = formData.getFirst(key);
            LOGGER.info("formdata key : {} value : {}",key,value);
        }

        Long jobId = Long.parseLong(formData.getFirst("jobId"));

        Job curJob = companyService.getJob(jobId);
        curJob.setDescription(formData.getFirst("description"));
        curJob.setLocation(formData.getFirst("location"));
        curJob.setTitle(formData.getFirst("title"));
        Boolean isResumeNeeded = formData.getFirst("isResumeNeeded").equals("yes");
        curJob.setIsResumeNeeded(isResumeNeeded);
        Boolean isCoverLetterNeeded = formData.getFirst("isCoverLetterNeeded").equals("yes");
        curJob.setIsCoverLetterNeeded(isCoverLetterNeeded);

        Long curTimestamp = System.currentTimeMillis();
        long expiryInDays = Long.parseLong(formData.getFirst("expiryTime"));
        Long expiryTimeStamp = curTimestamp+(expiryInDays *24*60*60*1000);
        curJob.setExpiryTime(expiryTimeStamp);
        curJob.setCreatedTime(curTimestamp);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Company companyDetails = userService.getCompanyDetails(user.getId());
        if( companyDetails.getId()!=curJob.getCompany().getId()){
            model.addAttribute("job", curJob);
            model.addAttribute("message","Job updation is not authorized.");
            return "company-add-job";
        }

        curJob = companyService.saveJob(curJob);

        String questions = formData.getFirst("questions");
        String questionArr[] = questions.split(",");

        companyService.deleteJobQuestions(jobId);

        List<JobQuestion> jobQuestions = new ArrayList<JobQuestion>();
        for( String question : questionArr ){
            JobQuestion jobQuestion = new JobQuestion();
            jobQuestion.setQuestionName(question);
            jobQuestion.setJob(curJob);
            jobQuestions.add(jobQuestion);
        }

        companyService.saveJobQuestions(jobQuestions);

        curJob.setExpiryTime(expiryInDays);

        model.addAttribute("job", curJob);
        model.addAttribute("questions",jobQuestions);
        model.addAttribute("message","Job updated successfully.");
        return "company-add-job";
    }


    @PostMapping(
            path = "/job/add",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String updateJob(@RequestBody MultiValueMap<String, String> formData, Model model){
        for( String key : formData.keySet() ){
            String value = formData.getFirst(key);
            LOGGER.info("formdata key : {} value : {}",key,value);
        }

        Job curJob = new Job();
        curJob.setDescription(formData.getFirst("description"));
        curJob.setLocation(formData.getFirst("location"));
        curJob.setTitle(formData.getFirst("title"));
        Boolean isResumeNeeded = formData.getFirst("isResumeNeeded").equals("yes");
        curJob.setIsResumeNeeded(isResumeNeeded);
        Boolean isCoverLetterNeeded = formData.getFirst("isCoverLetterNeeded").equals("yes");
        curJob.setIsCoverLetterNeeded(isCoverLetterNeeded);


        Long curTimestamp = System.currentTimeMillis();
        int expiryInDays = Integer.parseInt(formData.getFirst("expiryTime"));
        Long expiryTimeStamp = curTimestamp+ (long) expiryInDays *24*60*60*1000;
        curJob.setExpiryTime(expiryTimeStamp);
        curJob.setCreatedTime(curTimestamp);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Company companyDetails = userService.getCompanyDetails(user.getId());
        curJob.setCompany(companyDetails);

        curJob = companyService.saveJob(curJob);

        String questions = formData.getFirst("questions");
        String questionArr[] = questions.split(",");

        List<JobQuestion> jobQuestions = new ArrayList<JobQuestion>();
        for( String question : questionArr ){
            JobQuestion jobQuestion = new JobQuestion();
            jobQuestion.setQuestionName(question);
            jobQuestion.setJob(curJob);
            jobQuestions.add(jobQuestion);
        }

        companyService.saveJobQuestions(jobQuestions);

        model.addAttribute("job", new Job());
        model.addAttribute("message","Job created successfully.");
        return "company-add-job";
    }

    @DeleteMapping("/job")
    public String deleteDocument( @RequestParam("jobId")Long jobId, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            companyService.deleteJob(jobId);

            Company company = companyService.getCompanyByUserId(user.getId());
            List<Job> jobs = companyService.getCompanyJobs(company.getId());
            model.addAttribute("jobs",jobs);
            model.addAttribute("message","Job deleted successfully");
            return "company-job";

        } catch (Exception e) {
            LOGGER.error("Exception when uploading document : {}",e);
            return "company-job";
        }
    }

    @GetMapping("/profile")
    public String getJobseekerProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Company companyDetails = userService.getCompanyDetails(user.getId());
        model.addAttribute("company",companyDetails);
        return "company-admin-profile";
    }

    @PostMapping("/profile")
    public String updateJobSeekerProfile(@ModelAttribute("company") Company company, Model model, BindingResult bindingResult) throws UserAlreadyExistException {

        try{
            User updatedUser = company.getUser();
            User existingUser = userService.findById(updatedUser.getId());
            boolean isUserNameChanged = false, isPasswordChanged=false;
            if( !updatedUser.username.equals(existingUser.username) ){
                isUserNameChanged = true;
            }
            if( !updatedUser.password.isEmpty() ){
                isPasswordChanged=true;
            }
            userValidator.validateCompanyDetails(company,bindingResult,isUserNameChanged);

            if( bindingResult.hasErrors() ) {
                return "company-admin-profile";
            }

            String username, password;
            if( isPasswordChanged ){
                existingUser.password = updatedUser.password;
                existingUser.username = updatedUser.username;
                existingUser = userService.saveUser(existingUser);
            }else{
                existingUser.username = updatedUser.username;
                userService.updateUser(existingUser);
            }

            if( isPasswordChanged || isUserNameChanged ){
                Collection<SimpleGrantedAuthority> nowAuthorities =
                        (Collection<SimpleGrantedAuthority>)SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getAuthorities();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(existingUser.getUsername(), existingUser.getPassword(), nowAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            Company existingCompanyDetails = userService.getCompanyDetails(existingUser.getId());
            existingCompanyDetails.setName(company.getName());
            existingCompanyDetails.setAddress(company.getAddress());
            existingCompanyDetails.setContactInfo(company.getContactInfo());
            userService.saveCompanyDetails(existingCompanyDetails);

            model.addAttribute("company",company);
            model.addAttribute("message","Profile has been updated successfully");

        }catch (Exception ex){
            LOGGER.error("Exception in updating job seeker details : {0}",ex);
        }

        return "company-admin-profile";

    }
}
