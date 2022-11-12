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
                .provider((String)attributes.get("provider"))
                .nameAttributeKey(usernameAttributeName)
                .build();
    }
}
