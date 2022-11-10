package com.jobs.jobsearch.validator;

import com.jobs.jobsearch.model.Company;
import com.jobs.jobsearch.model.JobSeekerDetails;
import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.model.UserRegistrationForm;
import com.jobs.jobsearch.model.helper.UserRole;
import com.jobs.jobsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class UserValidator {
    @Autowired
    private UserService userService;

    private final static int MAX_STR_LENGTH=255;
    private final static String PHONE_REGEX="[0-9]{3}-[0-9]{3}-[0-9]{4}";

    private static boolean checkValidPhoneNumber(String phoneNum){
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        return pattern.matcher(phoneNum).matches();
    }

    private static void checkPassword(String password, String passwordConfirm, Errors errors){
        if (password.length() < 8 || password.length() > 32) {
            errors.rejectValue("user.password", "Size.userForm.password");
        }

        if ( !passwordConfirm.equals(password) ) {
            errors.rejectValue("user.passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }

    private static boolean checkStringLength(String str){
        return str.length()<MAX_STR_LENGTH;
    }
    private void checkUsername(String username, Errors errors){
        if (username.length() < 6 || username.length() > 32) {
            errors.rejectValue("user.username", "Size.userForm.username");
        }
        if (userService.findByUsername(username) != null) {
            errors.rejectValue("user.username", "Duplicate.userForm.username");
        }
    }

    public void validateJobSeekerDetails(JobSeekerDetails jobSeekerDetails ,  Errors errors, boolean isUsernameChanged){
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bio", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactInfo", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty");

        if( !checkValidPhoneNumber(jobSeekerDetails.getContactInfo()) ){
            errors.rejectValue("contactInfo", "contactInfo.badFormat");
        }

        if( !checkStringLength(jobSeekerDetails.getBio()) ){
            errors.rejectValue("bio", "stringExcessLength");
        }

        if( !checkStringLength(jobSeekerDetails.getAddress()) ){
            errors.rejectValue("address", "stringExcessLength");
        }

        User user = jobSeekerDetails.getUser();
        if( !user.password.isEmpty() ){
            checkPassword(user.getPassword(),user.getPasswordConfirm(),errors);
        }
        if( isUsernameChanged ){
            checkUsername(user.getUsername(),errors);
        }

    }



    public void validateCompanyDetails(Company company , Errors errors, boolean isUsernameChanged){

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactInfo", "NotEmpty");

        if( !checkValidPhoneNumber(company.getContactInfo()) ){
            errors.rejectValue("contactInfo", "contactInfo.badFormat");
        }

        if( !checkStringLength(company.getName()) ){
            errors.rejectValue("name", "stringExcessLength");
        }

        if( !checkStringLength(company.getAddress()) ){
            errors.rejectValue("address", "stringExcessLength");
        }

        User user = company.getUser();
        if( !user.password.isEmpty() ){
            checkPassword(user.getPassword(),user.getPasswordConfirm(),errors);
        }
        if( isUsernameChanged ){
            checkUsername(user.getUsername(),errors);
        }

    }

    public void validateRegistrationForm(UserRegistrationForm userForm, Errors errors) {

        User user = userForm.getUser();

        if( user.role== UserRole.JOB_SEEKER ){
            JobSeekerDetails jobSeekerDetails = userForm.getJobSeeker();
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobSeeker.bio", "NotEmpty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobSeeker.contactInfo", "NotEmpty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobSeeker.address", "NotEmpty");

            if( !checkValidPhoneNumber(jobSeekerDetails.getContactInfo()) ){
                errors.rejectValue("jobSeeker.contactInfo", "contactInfo.badFormat");
            }

            if( !checkStringLength(jobSeekerDetails.getBio()) ){
                errors.rejectValue("jobSeeker.bio", "stringExcessLength");
            }

            if( !checkStringLength(jobSeekerDetails.getAddress()) ){
                errors.rejectValue("jobSeeker.address", "stringExcessLength");
            }

        }else{

            Company company = userForm.getCompany();
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "company.name", "NotEmpty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "company.address", "NotEmpty");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "company.contactInfo", "NotEmpty");

            if( !checkValidPhoneNumber(company.getContactInfo()) ){
                errors.rejectValue("company.contactInfo", "contactInfo.badFormat");
            }

            if( !checkStringLength(company.getName()) ){
                errors.rejectValue("company.name", "stringExcessLength");
            }

            if( !checkStringLength(company.getAddress()) ){
                errors.rejectValue("company.address", "stringExcessLength");
            }

        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.username", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.password", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.email", "NotEmpty");

        checkUsername(user.getUsername(),errors);
        checkPassword(user.getPassword(),user.getPasswordConfirm(),errors);

    }
}
