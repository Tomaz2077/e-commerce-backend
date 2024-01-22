package com.tpm.ecommercebackend.service;

import com.tpm.ecommercebackend.exception.EmailFailureException;
import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails
 */
@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAdress;
    @Value("${app.frontend.url}")
    private String baseUrl;
    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Creates a new simple mail message
     * @return the SimpleMailMessage created
     */
    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAdress);
        return simpleMailMessage;
    }

    /**
     * Sends a verification email to the user
     * @param verificationToken the verification token to send
     * @throws EmailFailureException if the email could not be sent
     */
    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Please verify your email to activate your account");
        message.setText("Please follow the link below to verify your email to activate your account. \n" +
                baseUrl + "/auth/verify?token=" + verificationToken.getToken());
        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }

    public void sendResetPasswordEmail(LocalUser user, String token) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset your password");
        message.setText("You requested a password reset on our website. \n" +
                "Please follow the link below to reset your password. \n" +
                baseUrl + "/auth/reset?token=" + token);
        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }
}
