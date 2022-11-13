package com.jobs.jobsearch.validator;

import com.jobs.jobsearch.model.Job;
import com.jobs.jobsearch.model.JobDocument;
import com.jobs.jobsearch.model.JobQuestion;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.service.CompanyService;
import com.jobs.jobsearch.service.JobSeekerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyValidator {
    @Autowired
    private CompanyService companyService;

    private static final int titleLength = 32;

    private static final int MAX_LENGTH = 255;

    public Boolean validateJob(MultiValueMap<String, String> formData, Model model){

        boolean isValidationCorrect = true;

        ArrayList<String> jobErrorMessage = new ArrayList<String>();

        String title = formData.getFirst("title");
        if( title.isEmpty() ){
            isValidationCorrect=false;
            jobErrorMessage.add("Title should not be empty");
        }
        if( title.length()>titleLength ){
            isValidationCorrect=false;
            jobErrorMessage.add("Title should not be greater than 32");
        }
        if( !jobErrorMessage.isEmpty() ){
            model.addAttribute("jobTitleError",jobErrorMessage.toArray());
        }
        jobErrorMessage = new ArrayList<String>();

        String description = formData.getFirst("description");
        if( description.isEmpty() ){
            isValidationCorrect=false;
            jobErrorMessage.add("Description should not be empty");
        }
        if( description.length()>MAX_LENGTH ){
            isValidationCorrect=false;
            jobErrorMessage.add("Description should not be greater than "+MAX_LENGTH);
        }
        if( !jobErrorMessage.isEmpty() ){
            model.addAttribute("jobDescriptionError",jobErrorMessage.toArray());
        }
        jobErrorMessage = new ArrayList<String>();

        String location = formData.getFirst("location");
        if( location.isEmpty() ){
            isValidationCorrect=false;
            jobErrorMessage.add("Location should not be empty");
        }
        if( location.length()>titleLength ){
            isValidationCorrect=false;
            jobErrorMessage.add("Location should not be greater than 32");
        }
        if( !jobErrorMessage.isEmpty() ){
            model.addAttribute("jobLocationError",jobErrorMessage.toArray());
        }
        jobErrorMessage = new ArrayList<String>();

        String expiry = formData.getFirst("expiryTime");
        if( expiry.isEmpty() || !expiry.matches("^[0-9]*$") ){
            jobErrorMessage.add("Expiry date is invalid");
        }
        if( !jobErrorMessage.isEmpty() ){
            isValidationCorrect=false;
            model.addAttribute("jobExpiryError",jobErrorMessage.toArray());
        }

        jobErrorMessage = new ArrayList<String>();
        String questions = formData.getFirst("questions");

        if( !questions.isEmpty() ){
            JSONArray jsonArray = new JSONArray(formData.getFirst("questions"));
            for( int ind=0 ; ind<jsonArray.length() ; ++ind ){
                JSONObject questionObj = jsonArray.getJSONObject(ind);
                String questionVal = questionObj.getString("questionVal");
                if( questionVal.isEmpty() ){
                    isValidationCorrect=false;
                    jobErrorMessage.add("Question should not be empty");
                    break;
                }
                if( questionVal.length()>MAX_LENGTH ){
                    isValidationCorrect=false;
                    jobErrorMessage.add("Question should not be greater than "+MAX_LENGTH);
                    break;
                }
                if( !jobErrorMessage.isEmpty() ){
                    model.addAttribute("jobQuesitonError",jobErrorMessage.toArray());
                }
            }
        }


        return isValidationCorrect;
    }
}
