package com.morakmorak.morak_back_end.exception.webHook;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Getter
@Component
@NoArgsConstructor
public class ErrorNotificationGenerator {
    @Value("${discord.url}")
    String discordUrl;

    public void send(String errorMessage) {

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/json");
        HttpEntity<DiscordWebhookMessageForm> entity = new HttpEntity<>(DiscordWebhookMessageForm.builder()
                .avatarUrl("https://previews.123rf.com/images/arcady31/arcady311303/arcady31130300032/18519959-vector-oops-symbol.jpg")
                .content(errorMessage)
                .username("에러 전달 봇")
                .build(), headers);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.exchange(
                discordUrl,
                HttpMethod.POST,
                entity,
                String.class);
    }
}
