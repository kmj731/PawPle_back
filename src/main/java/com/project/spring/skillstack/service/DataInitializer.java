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
        

        // 유저 초기화 데이터
        
        List<UserEntity> userList = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String suffix = String.format("%02d", i);
            UserEntity user = new UserEntity(
                null,
                "user" + suffix,
                passwordEncoder.encode("1234"),
                "User " + suffix,
                List.of("USER"),
                "user" + suffix + "@pawple.com",
                "0101234" + String.format("%04d", i),
                null,
                LocalDateTime.now(),
                null,
                new ArrayList<>()
            );

            PetEntity pet1 = new PetEntity(
                "강아지",
                5.0 + i % 3,
                "댕댕이" + suffix,
                1 + i % 5,
                (i % 2 == 0) ? "수컷" : "암컷",
                "푸들",
                LocalDate.now().minusYears(1 + i % 3),
                user
            );

            PetEntity pet2 = new PetEntity(
                "고양이",
                4.0 + i % 2,
                "냐옹이" + suffix,
                2 + i % 4,
                (i % 2 == 0) ? "암컷" : "수컷",
                "러시안블루",
                LocalDate.now().minusYears(2 + i % 2),
                user
            );

            user.getPets().add(pet1);
            user.getPets().add(pet2);
            userList.add(user);
        }

        userRep.saveAll(userList);

        // userRep.save(new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, null));
        // userRep.save(new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", List.of("USER"), null, null, null, LocalDateTime.now(), null, null));
    }
}
