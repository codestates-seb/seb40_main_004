package com.morakmorak.morak_back_end.security.oauth;

import com.morakmorak.morak_back_end.entity.Role;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.entity.UserRole;
import com.morakmorak.morak_back_end.mapper.UserMapper;
import com.morakmorak.morak_back_end.repository.RoleRepository;
import com.morakmorak.morak_back_end.repository.UserRepository;
import com.morakmorak.morak_back_end.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.morakmorak.morak_back_end.entity.enums.RoleName.ROLE_USER;

@Component
@Transactional
@RequiredArgsConstructor
public class CustomOauth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Oauth2UserDto oauth2UserDto = Oauth2UserDto.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = userMapper.toOAuthEntity(oauth2UserDto);

        if ( userRepository.findUserByEmail(user.getEmail()).isEmpty() ) {
            saveUserAndAddBasicRole(user);
        }

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(ROLE_USER.toString())),
                oAuth2User.getAttributes(), userNameAttributeName);

        return defaultOAuth2User;
    }

    private void saveUserAndAddBasicRole(User user) {
        Role role_user = roleRepository.findRoleByRoleName(ROLE_USER);

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role_user)
                .build();

        userRoleRepository.save(userRole);
    }
}
