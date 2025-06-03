package com.project.spring.skillstack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

        UserEntity root = new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, "010-0000-0000", LocalDate.of(1999,9,9), null, null, LocalDateTime.now(), null, new ArrayList<>());
        UserEntity abcd = new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", roles, "abc123@pawple.com", "010-1234-5678", null, null, null, LocalDateTime.now(), null, new ArrayList<>());
        UserEntity vet = new UserEntity(null, "vet", passwordEncoder.encode("1234"), "vet", roles, "vet123@pawple.com", "010-4321-8765", null, null, null, LocalDateTime.now(), null, new ArrayList<>());

        abcd.setImageUrl("/test/iu.jpg");

        PetEntity abcdPet = new PetEntity("고양이", 4.0, "나비", 2025, "수컷", "코숏", LocalDate.now(), abcd);
        abcdPet.setImageUrl("/test/cat.jpg");
        abcd.getPets().add(abcdPet);
        PetEntity abcdPet2 = new PetEntity("강아지", 4.0, "바둑이", 2024, "암컷", "진돗개", LocalDate.now(), abcd);
        abcdPet2.setImageUrl("/test/dog.jpg");
        abcd.getPets().add(abcdPet2);

        PostEntity abcdPost1 = PostEntity.builder()
            .title("첫 글을 올립니다!")
            .content("안녕하세요. abcd입니다. 첫 글을 올립니다!")
            .category("일상")
            .user(abcd)
            .viewCount(150)
            .build();

        PostEntity abcdPost2 = PostEntity.builder()
            .title("반려동물 건강 관련해서 궁금한 점이 있습니다.")
            .content("반려동물 건강 관련해서 궁금한 점이 있습니다.")
            .category("Q&A")
            .user(abcd)
            .viewCount(181)
            .build();

        PostEntity abcdPost3 = PostEntity.builder()
            .title("강아지 눈곱이 자주 생기는데 정상인가요?")
            .content("강아지가 아침마다 눈곱이 많이 끼는데 걱정돼서 질문드립니다.")
            .category("Q&A")
            .user(abcd)
            .viewCount(220)
            .build();
            
        PostEntity abcdPost4 = PostEntity.builder()
            .title("고양이 스트레스 해소법 공유해요")
            .content("고양이가 요즘 예민해서 스트레스 해소 방법을 찾다가 성공한 경험을 공유합니다")
            .category("토픽")
            .user(abcd)
            .viewCount(251)
            .build();

        HealthCheckRecord record1 = new HealthCheckRecord();
        record1.setUserId(abcd.getId()); 
        record1.setTotalScore(80);
        record1.setResultStatus("양호");
        record1.setCheckedAt(LocalDateTime.now().minusDays(5));
        record1.setPet(abcdPet); 
        abcdPet.getHealthRecords().add(record1);

        HealthCheckRecord record2 = new HealthCheckRecord();
        record2.setUserId(abcd.getId());
        record2.setTotalScore(45);
        record2.setResultStatus("경고");
        record2.setCheckedAt(LocalDateTime.now().minusDays(2));
        record2.setPet(abcdPet2);
        abcdPet2.getHealthRecords().add(record2);

        userRep.save(root);
        userRep.save(abcd);
        userRep.save(vet);
        postRep.save(abcdPost1);
        postRep.save(abcdPost2);
        postRep.save(abcdPost3);
        postRep.save(abcdPost4);
        

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
                null, 
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

            int randomViewCount1 = ThreadLocalRandom.current().nextInt(1, 101);
            int randomViewCount2 = ThreadLocalRandom.current().nextInt(1, 101);

            LocalDateTime createdAt1 = LocalDate.of(2025, 5, 1)
                .plusDays(ThreadLocalRandom.current().nextInt(31))
                .atTime(ThreadLocalRandom.current().nextInt(24), ThreadLocalRandom.current().nextInt(60));

            LocalDateTime createdAt2 = LocalDate.of(2025, 5, 1)
                .plusDays(ThreadLocalRandom.current().nextInt(31))
                .atTime(ThreadLocalRandom.current().nextInt(24), ThreadLocalRandom.current().nextInt(60));

            PostEntity post1 = PostEntity.builder()
                .title("user" + suffix + "의 첫 번째 게시글입니다. 게시글 테스트 진행중입니다.")
                .content("user" + suffix + "의 첫 번째 게시글입니다.")
                .category("토픽")
                .user(user)
                .viewCount(randomViewCount1)
                .createdAt(createdAt1)
                .build();

            PostEntity post2 = PostEntity.builder()
                .title("user" + suffix + "의 두 번째 게시글입니다. 게시글 테스트 진행중입니다.")
                .content("user" + suffix + "의 두 번째 게시글입니다.")
                .category("일상")
                .user(user)
                .viewCount(randomViewCount2)
                .createdAt(createdAt2)
                .build();

            postList.add(post1);
            postList.add(post2);
        }

        postRep.saveAll(postList);

        // userRep.save(new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, null));
        // userRep.save(new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", List.of("USER"), null, null, null, LocalDateTime.now(), null, null));
    }
}
