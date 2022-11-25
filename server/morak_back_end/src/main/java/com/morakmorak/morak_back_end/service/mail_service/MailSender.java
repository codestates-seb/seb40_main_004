package com.morakmorak.morak_back_end.service.mail_service;

public interface MailSender {
    boolean sendMail(String to, String content, String title);
}
