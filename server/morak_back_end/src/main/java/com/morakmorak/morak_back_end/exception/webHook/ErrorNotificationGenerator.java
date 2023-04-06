package com.morakmorak.morak_back_end.exception.webHook;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorNotificationGenerator {
    private String errorMessage;

    public ErrorNotificationGenerator ErrorMessageInit(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public void send() {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/json");
        HttpEntity<DiscordWebhookMessageForm> entity = new HttpEntity<>(DiscordWebhookMessageForm.builder()
                .avatarUrl("https://previews.123rf.com/images/arcady31/arcady311303/arcady31130300032/18519959-vector-oops-symbol.jpg")
                .content(errorMessage)
                .username("에러 전달 봇")
                .build(), headers);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
                "https://discord.com/api/webhooks/1093427491739275264/RXNbPwMfTt8_IhBcjIUSIfniKsAQz8hTJt6pkyXFgGeX_jAI27s-5Z22bzlIjJk7lMSe",
                HttpMethod.POST,
                entity,
                String.class);
    }
}
