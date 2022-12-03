package com.morakmorak.morak_back_end.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class Oauth2UserDto {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;
    private String provider;

    public static Oauth2UserDto of(String registrationId,
                                   String usernameAttributeName,
                                   Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return ofGoogle(usernameAttributeName, attributes);
        } else if (registrationId.equals("kakao")) {
            return ofKakao(usernameAttributeName, attributes);
        }

        throw new IllegalArgumentException();
    }

    private static Oauth2UserDto ofGoogle(String usernameAttributeName,
                                          Map<String, Object> attributes) {
        return Oauth2UserDto.builder()
                .name((String)attributes.get("name"))
                .attributes(attributes)
                .email((String)attributes.get("email"))
                .picture((String)attributes.get("picture"))
                .provider("google")
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    private static Oauth2UserDto ofKakao(String usernameAttributeName,
                                         Map<String, Object> attributes) {
        return Oauth2UserDto.builder()
                .email((String)attributes.get("email"))
                .picture((String)attributes.get("profile_image_url"))
                .provider("kakao")
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    private void addRandomNickname(String randomNickname) {
        this.name = randomNickname;
    }
}
