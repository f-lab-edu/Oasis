package com.flab.oasis.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuth2Service extends DefaultOAuth2UserService {
    private static final String EMAIL = "email";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 구글 로그인에 성공하면 구글 서버로부터 유저 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글 유저 정보에서 uid(email)을 추출한다.
        Map<String, Object> memberAttribute = new HashMap<>();
        memberAttribute.put(EMAIL, oAuth2User.getAttributes().get(EMAIL).toString());

        // OAuth2 인증 정보를 등록한다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute,
                EMAIL // OAuth2User.getName() 메서드에서 반환할 attribute key
        );
    }
}
