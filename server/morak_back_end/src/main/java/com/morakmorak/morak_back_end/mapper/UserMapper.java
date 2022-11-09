package com.morakmorak.morak_back_end.mapper;

import com.morakmorak.morak_back_end.dto.AuthDto;
import com.morakmorak.morak_back_end.entity.User;
import com.morakmorak.morak_back_end.security.oauth.Oauth2UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toLoginEntity(AuthDto.RequestLogin dto);
    User toJoinEntity(AuthDto.RequestJoin dto);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "nickname")
    @Mapping(source = "provider", target = "provider")
    User toOAuthEntity(Oauth2UserDto dto);
}
