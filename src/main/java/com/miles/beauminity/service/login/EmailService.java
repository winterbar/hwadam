package com.miles.beauminity.service.login;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendVerificationEmail(String email) throws MessagingException;
    public boolean verifyCode(String email, String userInputCode);
}
