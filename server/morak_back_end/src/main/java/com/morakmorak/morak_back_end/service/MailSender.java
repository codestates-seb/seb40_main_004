package com.morakmorak.morak_back_end.service;

public interface MailSender {
    boolean sendMail(String to, String content, String title);
}
