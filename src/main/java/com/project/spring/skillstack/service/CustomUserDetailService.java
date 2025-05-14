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
        UserEntity user = userRep.findByNameLike(username).orElseThrow(()->new UsernameNotFoundException(username + "를 찾을 수 없음"));
        return new CustomUserDetails(user.toDto());
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        UserEntity user = new UserEntity();
        if(provider.equals("naver")) {
            Map<String, Object> attr = (Map<String, Object>)oAuth2User.getAttributes().get("response");
            Optional<UserEntity> getUser = userRep.findByNameLike(((String)attr.get("id")) + "_" + provider);
            if(getUser.isEmpty()) {
                user.setName(((String)attr.get("id")) + "_" + provider);
                user.setPass("Unknown");
                user.setSocialName("Social_" + Random.getRandomString(20));
                user.setRoles(List.of("USER"));
                user.setCreated(LocalDateTime.now());
                userRep.save(user);
            } else user = getUser.get();
            user.setAttr(attr);
        }

        return new CustomUserDetails(user.toDto());
    }
}
