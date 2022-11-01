package com.jobs.jobsearch.util;

import com.jobs.jobsearch.model.User;
import com.jobs.jobsearch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.UUID;

public class EmailSender {
    @Autowired
    private UserService userService;

    private JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("velsachin1998@gmail.com");
        mailSender.setPassword("zctgsxyybkvxlpau");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    public void sendConfirmationLink(User user, String message) {


        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message );
        JavaMailSender mailSender = getJavaMailSender();
        mailSender.send(email);
    }
}
