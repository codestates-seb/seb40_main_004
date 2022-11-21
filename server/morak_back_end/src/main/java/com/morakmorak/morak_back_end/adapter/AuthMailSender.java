package com.morakmorak.morak_back_end.adapter;

import com.morakmorak.morak_back_end.repository.RedisRepository;
import com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl;
import lombok.RequiredArgsConstructor;

import static com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl.BASIC_AUTH_SUBJECT;
import static com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl.BASIC_PASSWORD_SUBJECT;

@RequiredArgsConstructor
public class AuthMailSender {
    private final MailSenderImpl mailSender;
    private final RedisRepository<String> mailAuthKeyStore;
    private final RandomKeyGenerator randomKeyGenerator;

    private Boolean sendAuthenticationMail(String emailAddress, String randomKey) {
        return mailSender.sendMail(emailAddress, randomKey, BASIC_AUTH_SUBJECT);
    }

    private Boolean sendTemporaryPassword(String emailAddress, String randomKey) {
        return mailSender.sendMail(emailAddress, randomKey, BASIC_PASSWORD_SUBJECT);
    }
}
