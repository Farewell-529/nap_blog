package com.nap_blog.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String content,String form) throws MessagingException;
}
