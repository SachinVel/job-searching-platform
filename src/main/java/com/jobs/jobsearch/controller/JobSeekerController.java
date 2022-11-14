package com.jobs.jobsearch.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jobs.jobsearch.exception.UserAlreadyExistException;
import com.jobs.jobsearch.model.*;
import com.jobs.jobsearch.model.helper.ApplicationStatus;
import com.jobs.jobsearch.model.helper.DocType;
import com.jobs.jobsearch.service.CompanyService;
import com.jobs.jobsearch.service.JobSeekerService;
import com.jobs.jobsearch.service.JobSeekerServiceImpl;
import com.jobs.jobsearch.service.UserService;
import com.jobs.jobsearch.validator.JobSeekerValidator;
import com.jobs.jobsearch.validator.UserValidator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

@Controller
@RequestMapping("seeker")
public class JobSeekerController {
    private static final Logger LOGGER= LoggerFactory.getLogger(JobSeekerController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JobSeekerService jobSeekerService;

    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserValidator userValidator;

    @Autowired
    private JobSeekerValidator jobSeekerValidator;

    @Value("${project.location}")
    private String documentLocation;

    @GetMapping("/index")
    public String jobseekerhome(){
        return "job-seeker";
    }

    @GetMapping("/job")
    public String getAllJobs(Model model){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<Job> jobs = companyService.getAllJobs();
            model.addAttribute("jobs",jobs);
            LOGGER.info("Company jobs are fetched by user : {}",username);
            return "job-seeker-job";
        }catch (Exception ex){
            LOGGER.error("Exception while viewing jobs {}",ex);
            model.addAttribute("message","Error occurred while viewing job application");
            return "error";
        }
    }

    @GetMapping("/application/{jobId}")
    public String getJobApplication(@PathVariable("jobId")Long jobId,Model model){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            Job job = companyService.getJob(jobId);
            Long expiryTimestamp = job.getExpiryTime();
            double expiryInDays = (int)(expiryTimestamp/(24*60*60*1000))-(System.currentTimeMillis()/(24*60*60*1000));
            long days = (int)Math.ceil(expiryInDays);
            job.setExpiryTime(days);

            List<JobApplication> jobApplications = companyService.getJobApplicationByJobId(jobId);
            for( JobApplication jobApplication : jobApplications ){
                if( jobApplication.getUser().getId()==user.getId() ){
                    model.addAttribute("job",job);
                    model.addAttribute("message","You already applied to this job!");
                    return "job-seeker-job-detail";
                }
            }

            List<JobQuestion> jobQuestions = companyService.getJobQuestions(jobId);

            List<JobDocument> userDocuments = jobSeekerService.getUserDocuments(user.getId());

            model.addAttribute("job",job);
            model.addAttribute("questions",jobQuestions);
            model.addAttribute("documents",userDocuments);
            LOGGER.info("Job applications of jobId {} is accessed by user : {}",jobId,user.getUsername());
            return "job-seeker-add-application";
        }catch (Exception ex){
            LOGGER.error("Exception while viewing job application {}",ex);
            model.addAttribute("message","Error occurred while viewing job application");
            return "error";
        }

    }

    @GetMapping("/application")
    public String getJobApplications(Model model){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            List<JobApplication> userJobApplications = jobSeekerService.getUserJobApplications(user.getId());

            model.addAttribute("jobApplications",userJobApplications);
            return "job-seeker-application";
        }catch (Exception ex){
            LOGGER.error("Exception while viewing job applications {}",ex);
            model.addAttribute("message","Error occurred while viewing job applications");
            return "error";
        }


    }

    @DeleteMapping("/application/{applicationId}")
        public String deleteJobApplications(@PathVariable("applicationId")Long applicationId,Model model){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            List<JobApplication> userJobApplications = jobSeekerService.getUserJobApplications(user.getId());
            JobApplication jobApplication = jobSeekerService.getApplication(applicationId);
            if( jobApplication.getUser().getId()!=user.getId() ){
                model.addAttribute("message","Unauthorized access");
                return "error";
            }
            jobSeekerService.deleteJobApplication(applicationId);

            userJobApplications = jobSeekerService.getUserJobApplications(user.getId());

            model.addAttribute("message","The application has been withdrawn successfully");
            model.addAttribute("jobApplications",userJobApplications);
            return "job-seeker-application";
        }catch (Exception ex){
            LOGGER.error("Exception while deleting job application {}",ex);
            model.addAttribute("message","Error occurred while viewing job applications");
            return "error";
        }


    }

    @PostMapping(
            path = "/application",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public String submitApplication(@RequestBody MultiValueMap<String, String> formData, Model model){

        try{
            String answersStr = formData.getFirst("answers");
            JSONArray answerArr = new JSONArray(answersStr);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            JobApplication jobApplication = new JobApplication();
            Long jobId = Long.parseLong(formData.getFirst("jobId"));
            Job job = companyService.getJob(jobId);
            jobApplication.setApplicationStatus(ApplicationStatus.IN_PROGRESS);
            jobApplication.setJob(job);
            jobApplication.setUser(user);
            Long resumeDocId = Long.parseLong(formData.getFirst("resumeId"));
            JobDocument resumeDocument = jobSeekerService.getDocument(resumeDocId);
            jobApplication.setResumeDocument(resumeDocument);
            Long coverLetterDocId = Long.parseLong(formData.getFirst("coverLetterId"));
            JobDocument coverLetterDocument = jobSeekerService.getDocument(coverLetterDocId);
            jobApplication.setCoverLetterDocument(coverLetterDocument);

            jobApplication = jobSeekerService.saveApplication(jobApplication);

            List<JobAnswer> jobAnswers = new ArrayList<JobAnswer>();
            for( int ind=0 ; ind<answerArr.length() ; ++ind ){
                JSONObject answerObj = answerArr.getJSONObject(ind);
                JobAnswer jobAnswer = new JobAnswer();
                jobAnswer.setAnswerValue(answerObj.getString("value"));
                JobQuestion jobQuestion = companyService.getJobQuestionById(answerObj.getLong("questionId"));
                jobAnswer.setJobQuestion(jobQuestion);
                jobAnswer.setJobApplication(jobApplication);
                jobAnswers.add(jobAnswer);
            }
            jobSeekerService.saveJobAnswers(jobAnswers);
            model.addAttribute("message","Application has been submitted successfully");
        }catch (Exception ex){
            LOGGER.error("Exception during application submission : {}",ex);
            model.addAttribute("message","There is some error in submitting application");
        }


        return "job-seeker-application-result";
    }




    @GetMapping("/job/{jobId}")
    public String getJobDetails(@PathVariable("jobId")Long jobId,Model model){
        try{
            Job job = companyService.getJob(jobId);
            Long expiryTimestamp = job.getExpiryTime();
            double expiryInDays = (int)(expiryTimestamp/(24*60*60*1000))-(System.currentTimeMillis()/(24*60*60*1000));
            long days = (int)Math.ceil(expiryInDays);
            job.setExpiryTime(days);
            model.addAttribute("job",job);
            return "job-seeker-job-detail";
        }catch (Exception ex){
            LOGGER.error("Exception during viewing job details : {}",ex);
            model.addAttribute("message","There is some error in viewing job details");
            return "error";
        }

    }

    @GetMapping("/profile")
    public String getJobseekerProfile(Model model){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            JobSeekerDetails curJobseekerDetails = userService.getJobSeekerDetails(user.getId());
            model.addAttribute("jobSeekerDetails",curJobseekerDetails);
            return "job-seeker-profile";
        }catch (Exception ex){
            LOGGER.error("Exception during viewing  profile : {}",ex);
            model.addAttribute("message","There is some error in viewing profile");
            return "error";
        }

    }

    @PostMapping("/profile")
    public String updateJobSeekerProfile(@ModelAttribute("jobSeekerDetails") JobSeekerDetails jobSeekerDetails,  Model model, BindingResult bindingResult) throws UserAlreadyExistException {

        try{

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if( user.getId()!=jobSeekerDetails.getUser().getId() ){
                model.addAttribute("message","Unauthorized access");
                return "error";
            }
            User updatedUser = jobSeekerDetails.getUser();
            User existingUser = userService.findById(updatedUser.getId());
            boolean isUserNameChanged = false, isPasswordChanged=false;
            if( !updatedUser.username.equals(existingUser.username) ){
                isUserNameChanged = true;
            }
            if( !updatedUser.password.isEmpty() ){
                isPasswordChanged=true;
            }
            userValidator.validateJobSeekerDetails(jobSeekerDetails,bindingResult,isUserNameChanged);

            if( bindingResult.hasErrors() ) {
                return "job-seeker-profile";
            }

            String password;
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
                authentication =
                        new UsernamePasswordAuthenticationToken(existingUser.getUsername(), existingUser.getPassword(), nowAuthorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            JobSeekerDetails existingJobSeekerDetails = userService.getJobSeekerDetails(existingUser.getId());
            existingJobSeekerDetails.setBio(jobSeekerDetails.getBio());
            existingJobSeekerDetails.setAddress(jobSeekerDetails.getAddress());
            existingJobSeekerDetails.setContactInfo(jobSeekerDetails.getContactInfo());
            userService.saveJobSeekerDetails(existingJobSeekerDetails);

            model.addAttribute("jobSeekerDetails",jobSeekerDetails);
            model.addAttribute("message","Profile has been updated successfully");
            return "job-seeker-profile";
        }catch (Exception ex){
            LOGGER.error("Exception in updating job seeker details : {0}",ex);
            return "error";
        }



    }

    @GetMapping("/document")
    public String getJobDocument(Model model){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            List<JobDocument> jobDocuments = jobSeekerService.getUserDocuments(user.getId());
            model.addAttribute("jobDocuments",jobDocuments);
            return "job-seeker-document";
        }catch (Exception ex){
            LOGGER.error("Exception in updating job Documents : {0}",ex);
            return "error";
        }


    }

    @PostMapping("/document/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file, @RequestParam("docType")DocType docType, Model model) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            boolean isValidationCorrect = jobSeekerValidator.validateDocument(user, file,model);

            if( !isValidationCorrect ) {
                List<JobDocument> jobDocuments = jobSeekerService.getUserDocuments(user.getId());
                model.addAttribute("jobDocuments",jobDocuments);
                return "job-seeker-document";
            }

            String userLocation = documentLocation+"/"+user.getId();
            Files.createDirectories(Paths.get(userLocation));
            var fileName = file.getOriginalFilename();
            String normalizedFileName = Paths.get(fileName).normalize().toString();
            var is = file.getInputStream();
            Files.copy(is, Paths.get(userLocation +"/"+ normalizedFileName),
                    StandardCopyOption.REPLACE_EXISTING);

            JobDocument jobDocument = new JobDocument();
            jobDocument.setType(docType);
            jobDocument.setName(normalizedFileName);
            jobDocument.setUser(user);
            jobSeekerService.saveDocument(jobDocument);

            List<JobDocument> jobDocuments = jobSeekerService.getUserDocuments(user.getId());
            model.addAttribute("jobDocuments",jobDocuments);

            model.addAttribute("message","Document has been uploaded successfully");
            return "job-seeker-document";

        } catch (Exception e) {

            LOGGER.error("Exception when uploading document : {}",e);
            model.addAttribute("message","Error while upload document. Please Try again later.");
            return "error";
        }
    }

    @GetMapping("/document/download/{docId}")
    public ResponseEntity<?> downloadFile(@PathVariable("docId") Long docId) {

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userService.findByUsername(username);

            JobDocument curDoc = jobSeekerService.getDocument(docId);
            User documentUser = curDoc.getUser();
            if( user.getId()!=documentUser.getId() ){
                return ResponseEntity.badRequest().build();
            }


            String docLocation = documentLocation+"/"+user.getId()+"/"+curDoc.getName();
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


    @DeleteMapping("/document")
    public String deleteDocument( @RequestParam("docId")Long docId, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = userService.findByUsername(username);

            JobDocument curDoc = jobSeekerService.getDocument(docId);
            User documentUser = curDoc.getUser();
            if( user.getId()!=documentUser.getId() ){
                return "redirect:/seeker/document";
            }

            jobSeekerService.deleteDocument(docId);

            String userLocation = documentLocation+"/"+user.getId();
            Files.delete(Paths.get(userLocation +"/"+ curDoc.getName()));

            List<JobDocument> jobDocuments = jobSeekerService.getUserDocuments(user.getId());
            model.addAttribute("jobDocuments",jobDocuments);

            model.addAttribute("message","Document has been deleted successfully");
            return "job-seeker-document";

        } catch (Exception e) {
            LOGGER.error("Exception when uploading document : {}",e);
            model.addAttribute("message","Error in deleting document");
            return "error";
        }
    }

}
