package com.morakmorak.morak_back_end.exception.webHook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscordWebhookMessageForm {
    @JsonProperty("username")
    private String username;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("content")
    private String content;
}
