package com.jobs.jobsearch.validator;

import com.jobs.jobsearch.model.JobDocument;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.service.JobSeekerService;
import com.jobs.jobsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class JobSeekerValidator {
    private static final int MAX_FILE_SIZE = 1000000;

    private static final int MAX_USER_UPLOADS = 5;

    private static final String[] allowedExtension = {"pdf","docx"};

    @Autowired
    private JobSeekerService jobSeekerService;
    public Boolean validateDocument(User user, MultipartFile file, Model model){
        boolean isValidationCorrect = true;
        List<JobDocument> userDocuments = jobSeekerService.getUserDocuments(user.getId());
        ArrayList<String> documentErrorMessage = new ArrayList<String>();

        boolean isDocumentExists = false;
        for( JobDocument jobDocument : userDocuments ){
            if( jobDocument.getName().equals(file.getOriginalFilename()) ){
                isDocumentExists = true;
            }
        }
        if( isDocumentExists ){
            isValidationCorrect = false;
            documentErrorMessage.add("The File name already exists");
        }
        if( userDocuments.size()>=MAX_USER_UPLOADS ){
            isValidationCorrect = false;
            documentErrorMessage.add("User cannot upload more than 5 files");
        }
        if( file.getSize()>MAX_FILE_SIZE ){
            isValidationCorrect = false;
            documentErrorMessage.add("The File size should be less than 1 MB");
        }

        boolean isExtensionValid=false;
        for( String extension : allowedExtension ){
            if (file.getOriginalFilename().endsWith(extension) ){
                isExtensionValid = true;
            }
        }
        if( !isExtensionValid ){
            isValidationCorrect = false;
            documentErrorMessage.add("Only DOCX and PDF files are allowed");
        }


        if( !isValidationCorrect ){
            model.addAttribute("jobDocumentError",documentErrorMessage.toArray());
        }
        return isValidationCorrect;
    }
}
