package com.morakmorak.morak_back_end.service;
import com.morakmorak.morak_back_end.dto.EmailDto;

public interface AuthMailSender {
    boolean sendEmail(EmailDto.RequestSendMail emailDto, String authKey);
}
