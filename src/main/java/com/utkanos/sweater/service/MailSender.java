package com.utkanos.sweater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    //метод для отправки сообщения
    //эта аннотация даст возможность ассинхронно отправлять сообщение, чтобы пользователь не ждал, пока соо отправится
    @Async
    public void sendMail(String subject, String emailTo, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailMessage.setTo(emailTo);
        mailMessage.setFrom(username);

        mailSender.send(mailMessage);
    }
}
