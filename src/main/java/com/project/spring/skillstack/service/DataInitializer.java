package com.project.spring.skillstack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRep;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {


        UserEntity root = new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, new ArrayList<>());
        UserEntity abcd = new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", List.of("USER"), "abc123@pawple.com", "01012345678", null, LocalDateTime.now(), null, new ArrayList<>());

        PetEntity abcdPet = new PetEntity("고양이", 4.0, "나비", 2, "수컷", "코숏", LocalDate.now(), abcd);
        abcd.getPets().add(abcdPet);
        PetEntity abcdPet2 = new PetEntity("강아지", 4.0, "바둑이", 3, "암컷", "진돗개", LocalDate.now(), abcd);
        abcd.getPets().add(abcdPet2);

        userRep.save(root);
        userRep.save(abcd);
        
        // userRep.save(new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, null));
        // userRep.save(new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", List.of("USER"), null, null, null, LocalDateTime.now(), null, null));
    }
}
