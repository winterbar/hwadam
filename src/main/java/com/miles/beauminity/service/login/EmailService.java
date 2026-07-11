package com.miles.beauminity.service.login;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendVerificationEmail(String email, String type) throws MessagingException;
    public void sendNoticeEmail(String email, String newPassword) throws MessagingException;
    public boolean verifyCode(String email, String type, String userInputCode);
    public String createPassword();
}
