package com.project.spring.skillstack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.HealthCheckRecord;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.UserEntity;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRep;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PostRepository postRep;

    @Override
    public void run(String... args) throws Exception {

        List<String> roles = new ArrayList<>();
        roles.add("USER");

        UserEntity root = new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, new ArrayList<>());
        UserEntity abcd = new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", roles, "abc123@pawple.com", "010-1234-5678", null, LocalDateTime.now(), null, new ArrayList<>());
        UserEntity vet = new UserEntity(null, "vet", passwordEncoder.encode("1234"), "vet", roles, "vet123@pawple.com", "010-4321-8765", null, LocalDateTime.now(), null, new ArrayList<>());

        PetEntity abcdPet = new PetEntity("고양이", 4.0, "나비", 2, "수컷", "코숏", LocalDate.now(), abcd);
        abcd.getPets().add(abcdPet);
        PetEntity abcdPet2 = new PetEntity("강아지", 4.0, "바둑이", 3, "암컷", "진돗개", LocalDate.now(), abcd);
        abcd.getPets().add(abcdPet2);

        PostEntity abcdPost1 = PostEntity.builder()
            .title("abcd의 첫 번째 게시글")
            .content("안녕하세요. abcd입니다. 첫 글을 올립니다!")
            .category("건강토픽")
            .user(abcd)
            .build();

        PostEntity abcdPost2 = PostEntity.builder()
            .title("abcd의 두 번째 게시글")
            .content("반려동물 건강 관련해서 궁금한 점이 있습니다.")
            .category("Q&A")
            .user(abcd)
            .build();
            
        HealthCheckRecord record1 = new HealthCheckRecord();
        record1.setUserId(abcd.getId()); // 아직 ID는 null이지만 persist 시 자동 반영됨
        record1.setTotalScore(80);
        record1.setResultStatus("양호");
        record1.setCheckedAt(LocalDateTime.now().minusDays(5));
        record1.setPet(abcdPet); // 연결
        abcdPet.getHealthRecords().add(record1);

        HealthCheckRecord record2 = new HealthCheckRecord();
        record2.setUserId(abcd.getId());
        record2.setTotalScore(45);
        record2.setResultStatus("경고");
        record2.setCheckedAt(LocalDateTime.now().minusDays(2));
        record2.setPet(abcdPet2);
        abcdPet2.getHealthRecords().add(record2);

        abcd.getPets().add(abcdPet);
        abcd.getPets().add(abcdPet2);

        userRep.save(root);
        userRep.save(abcd);
        userRep.save(vet);
        postRep.save(abcdPost1);
        postRep.save(abcdPost2);
        

        // 유저 초기화 데이터
        
        List<UserEntity> userList = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String suffix = String.format("%02d", i);
            UserEntity user = new UserEntity(
                null,
                "user" + suffix,
                passwordEncoder.encode("1234"),
                "User " + suffix,
                roles,
                "user" + suffix + "@pawple.com",
                "010-1234-" + String.format("%04d", i),
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

        // 게시글 초기화
        List<PostEntity> postList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            UserEntity user = userList.get(i);
            String suffix = String.format("%02d", i + 1);

            PostEntity post1 = PostEntity.builder()
                .title("제목 예시 A - user" + suffix)
                .content("user" + suffix + "의 첫 번째 게시글입니다.")
                .category("건강토픽")
                .user(user)
                .build();

            PostEntity post2 = PostEntity.builder()
                .title("제목 예시 B - user" + suffix)
                .content("user" + suffix + "의 두 번째 게시글입니다.")
                .category("일상")
                .user(user)
                .build();

            postList.add(post1);
            postList.add(post2);
        }

        postRep.saveAll(postList);

        // userRep.save(new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, null));
        // userRep.save(new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", List.of("USER"), null, null, null, LocalDateTime.now(), null, null));
    }
}
