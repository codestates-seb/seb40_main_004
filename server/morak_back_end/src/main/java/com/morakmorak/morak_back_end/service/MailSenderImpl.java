package com.morakmorak.morak_back_end.service;

import com.morakmorak.morak_back_end.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.morakmorak.morak_back_end.exception.ErrorCode.AUTH_KEY_ALREADY_EXISTS;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.AUTH_KEY_EXPIRATION_PERIOD;
import static com.morakmorak.morak_back_end.security.util.SecurityConstants.EMAIL_AUTH_KEY_LENGTH;

@Service
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public final static String BASIC_AUTH_SUBJECT = "모락모락의 이메일 인증 번호입니다.";
    public final static String BASIC_PASSWORD_SUBJECT = "모락모락의 임시 비밀번호입니다.";
    @Override
    public boolean sendMail(String to, String content, String title) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailProperties.getUsername());
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(content);
        mailSender.send(simpleMailMessage);

        return true;
    }
}
