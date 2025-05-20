package com.project.spring.skillstack.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.utility.Random;

@Service
public class CustomUserDetailService extends DefaultOAuth2UserService implements UserDetailsService {

    @Autowired
    UserRepository userRep;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRep.findByName(username).orElseThrow(()->new UsernameNotFoundException(username + "를 찾을 수 없음"));
        return new CustomUserDetails(user.toDto());
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        UserEntity user = new UserEntity();

        Map<String, Object> attr = oAuth2User.getAttributes();
        String userId;
        String socialName;

        switch (provider) {
            case "naver":
                attr = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                userId = (String) attr.get("id");
                socialName = "Social_Naver_" + Random.getRandomString(20);
                break;

            case "kakao":
                attr = (Map<String, Object>) oAuth2User.getAttributes().get("properties");
                userId = oAuth2User.getAttributes().get("id").toString();
                socialName = "Social_Kakao_" + Random.getRandomString(20);
                break;

            case "google":
                userId = (String) oAuth2User.getAttributes().get("sub");
                socialName = "Social_Google_" + Random.getRandomString(20);
                break;

            default:
                throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        Optional<UserEntity> getUser = userRep.findByName(userId + "_" + provider);
        if (getUser.isEmpty()) {
            user.setName(userId + "_" + provider);
            user.setPass("Unknown");
            user.setName(socialName);
            user.setRoles(List.of("USER"));
            user.setCreated(LocalDateTime.now());
            userRep.save(user);
        } else {
            user = getUser.get();
        }
        user.setAttr(attr);

        return new CustomUserDetails(user.toDto());
    }
}
