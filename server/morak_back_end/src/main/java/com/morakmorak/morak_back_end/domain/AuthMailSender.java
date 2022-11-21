package com.morakmorak.morak_back_end.domain;

import com.morakmorak.morak_back_end.repository.redis.RedisRepository;
import com.morakmorak.morak_back_end.service.mail_service.MailSender;
import lombok.RequiredArgsConstructor;

import static com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl.BASIC_AUTH_SUBJECT;
import static com.morakmorak.morak_back_end.service.mail_service.MailSenderImpl.BASIC_PASSWORD_SUBJECT;

// TODO : 추후 코드 리팩터링을 위해 만들어졌으며 아직 사용되지 않는 클래스입니다.
@RequiredArgsConstructor
public class AuthMailSender {
    private final MailSender mailSender;
    private final RedisRepository<String> mailAuthKeyStore;
    private final RandomKeyGenerator randomKeyGenerator;

    private Boolean sendAuthenticationMail(String emailAddress, String randomKey) {
        return mailSender.sendMail(emailAddress, randomKey, BASIC_AUTH_SUBJECT);
    }

    private Boolean sendTemporaryPassword(String emailAddress, String randomKey) {
        return mailSender.sendMail(emailAddress, randomKey, BASIC_PASSWORD_SUBJECT);
    }
}
