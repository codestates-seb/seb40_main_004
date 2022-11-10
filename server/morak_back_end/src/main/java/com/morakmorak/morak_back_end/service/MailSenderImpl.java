package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMailSenderImpl implements AuthMailSender {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    private final String BASIC_SUBJECT = "모락모락의 이메일 인증 번호입니다.";
    @Override
    public boolean sendMail(EmailDto.RequestSendMail emailDto, String authKey) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getUsername());
        simpleMailMessage.setTo(emailDto.getEmail());
        simpleMailMessage.setSubject(BASIC_SUBJECT);
        simpleMailMessage.setText(authKey);
        mailSender.send(simpleMailMessage);

        return true;
    }
}
