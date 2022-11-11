package com.jobs.jobsearch.validator;

import com.jobs.jobsearch.model.JobDocument;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.service.JobSeekerService;
import com.jobs.jobsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public class JobSeekerValidator {
    private static final int MAX_FILE_SIZE = 1000000;

    private static final int MAX_USER_UPLOADS = 5;

    @Autowired
    private static JobSeekerService jobSeekerService;
    public static void validateDocument(User user, MultipartFile file, Errors errors){
        List<JobDocument> userDocuments = jobSeekerService.getUserDocuments(user.getId());
        if( userDocuments.size()>=MAX_USER_UPLOADS ){
            errors.rejectValue("jobDocument", "document.maxUpload");
        }
        if( file.getSize()>MAX_FILE_SIZE ){
            errors.rejectValue("jobDocument", "document.maxFileSize");
        }
    }
}
