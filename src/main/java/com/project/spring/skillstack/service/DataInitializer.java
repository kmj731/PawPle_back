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

import com.project.spring.skillstack.dao.CommentRepository;
import com.project.spring.skillstack.dao.PetRepository;
import com.project.spring.skillstack.dao.PostRepository;
import com.project.spring.skillstack.dao.ProductRepository;
import com.project.spring.skillstack.dao.UserRepository;
import com.project.spring.skillstack.entity.CommentEntity;
import com.project.spring.skillstack.entity.ConsultEntity;
import com.project.spring.skillstack.entity.HealthCheckRecord;
import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.PostEntity;
import com.project.spring.skillstack.entity.ProductEntity;
import com.project.spring.skillstack.entity.UserEntity;
import com.project.spring.skillstack.entity.VaccinationRecord;
import com.project.spring.skillstack.repository.ConsultRepository;
import com.project.spring.skillstack.repository.HealthCheckRecordRepository;
import com.project.spring.skillstack.repository.VaccinationRecordRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository userRep;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PostRepository postRep;
    @Autowired
    CommentRepository commentRep;
    @Autowired
    VaccinationRecordRepository vaccineRep;
    @Autowired
    PetRepository petRep;
    @Autowired
    HealthCheckRecordRepository healthRecordRep;
    @Autowired
    ConsultRepository consultRep;
    @Autowired
    ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {

        if (userRep.existsByName("root")) {
            System.out.println("[DataInitializer] 'root' 유저가 이미 존재합니다. 초기화를 생략합니다.");
            return;
        }

        List<String> roles = new ArrayList<>();
        roles.add("USER");
        List<String> roles2 = new ArrayList<>();
        roles2.add("VET");

        UserEntity root = new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, "010-0000-0000", LocalDate.of(1999,9,9), null, null, LocalDateTime.now(), null, new ArrayList<>(),99999);
        UserEntity abcd = new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", roles, "abc123@pawple.com", "010-1234-5678", null, null, null, LocalDateTime.now(), null, new ArrayList<>(),100);
        UserEntity vet = new UserEntity(null, "vet", passwordEncoder.encode("1234"), "vet", roles2, "vet123@pawple.com", "010-4321-8765", null, null, null, LocalDateTime.now(), null,new ArrayList<>(),0);


        PetEntity abcdPet = new PetEntity("고양이", 4.0, "나비", 2025, "수컷", "코숏", LocalDate.now(), abcd);
        abcd.getPets().add(abcdPet);
        PetEntity abcdPet2 = new PetEntity("강아지", 4.0, "바둑이", 2024, "암컷", "진돗개", LocalDate.now(), abcd);
        abcd.getPets().add(abcdPet2);

        userRep.save(root);
        userRep.save(abcd);
        userRep.save(vet);

        VaccinationRecord vaccine1 = VaccinationRecord.builder()
            .pet(abcdPet)
            .step(1)
            .vaccineName("1차접종(종합백신+코로나 장염)")
            .vaccinatedAt(LocalDate.of(2025, 6, 6))
            .nextVaccinationDate(LocalDate.of(2025, 6, 20))
            .build();

        VaccinationRecord vaccine2 = VaccinationRecord.builder()
            .pet(abcdPet)
            .step(2)
            .vaccineName("2차접종(종합백신+코로나 장염)")
            .vaccinatedAt(LocalDate.of(2025, 6, 20))
            .nextVaccinationDate(LocalDate.of(2025, 7, 4))
            .build();

        VaccinationRecord vaccine3 = VaccinationRecord.builder()
            .pet(abcdPet2)
            .step(1)
            .vaccineName("1차접종(종합백신+코로나 장염)")
            .vaccinatedAt(LocalDate.of(2025, 6, 1))
            .nextVaccinationDate(LocalDate.of(2025, 6, 15))
            .build();

        petRep.saveAll(List.of(abcdPet, abcdPet2));
        vaccineRep.saveAll(List.of(vaccine1, vaccine2, vaccine3));

        PostEntity abcdPost1 = PostEntity.builder()
            .title("첫 글을 올립니다!")
            .content("안녕하세요. abcd입니다. 첫 글을 올립니다!")
            .category("일상")
            .user(abcd)
            .viewCount(150)
            .commentCount(1)
            .build();

        PostEntity abcdPost2 = PostEntity.builder()
            .title("반려동물 건강 관련해서 궁금한 점이 있습니다.")
            .content("반려동물 건강 관련해서 궁금한 점이 있습니다.")
            .category("Q&A")
            .user(abcd)
            .viewCount(181)
            .commentCount(1)
            .build();

        PostEntity abcdPost3 = PostEntity.builder()
            .title("강아지 눈곱이 자주 생기는데 정상인가요?")
            .content("강아지가 아침마다 눈곱이 많이 끼는데 걱정돼서 질문드립니다.")
            .category("Q&A")
            .user(abcd)
            .viewCount(220)
            .commentCount(1)
            .build();
            
        PostEntity abcdPost4 = PostEntity.builder()
            .title("고양이 스트레스 해소법 공유해요")
            .content("고양이가 요즘 예민해서 스트레스 해소 방법을 찾다가 성공한 경험을 공유합니다")
            .category("토픽")
            .subCategory("행동")
            .user(abcd)
            .viewCount(251)
            .commentCount(1)
            .build();

        postRep.save(abcdPost1);
        postRep.save(abcdPost2);
        postRep.save(abcdPost3);
        postRep.save(abcdPost4);

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
        
        healthRecordRep.save(record1);
        healthRecordRep.save(record2);

        ConsultEntity abcdConsult1 = ConsultEntity.builder()
            .title("고양이 나비가 밥을 잘 안 먹어요")
            .content("안녕하세요. 고양이 나비가 요즘 밥을 잘 먹지 않고 기운이 없어 보여 걱정입니다. 사료를 바꿔야 할지 병원을 가야 할지 고민됩니다.")
            .createdAt(LocalDateTime.now().minusDays(3))
            .status("대기")
            .subCategory("식이관리")
            .user(abcd)
            .pet(abcdPet)
            .build();

        ConsultEntity abcdConsult2 = ConsultEntity.builder()
            .title("강아지 바둑이가 산책 중 다리를 절어요")
            .content("최근에 바둑이와 산책을 나갔는데, 갑자기 한쪽 다리를 들고 절기 시작했습니다. 혹시 어디 삐었을까요? 어떻게 조치해야 할까요?")
            .createdAt(LocalDateTime.now().minusDays(1))
            .status("대기")
            .subCategory("행동")
            .user(abcd)
            .pet(abcdPet2)
            .build();

        consultRep.save(abcdConsult1);
        consultRep.save(abcdConsult2);

        UserEntity qwer = new UserEntity(null, "qwer", passwordEncoder.encode("1234"), "qwer", roles, "qwer123@pawple.com", "010-5678-1234", null, null, null, LocalDateTime.now(), null, new ArrayList<>(),999);

        PetEntity qwerPet1 = new PetEntity("강아지", 5.5, "초코", 2023, "수컷", "푸들", LocalDate.now(), qwer);
        qwer.getPets().add(qwerPet1);
        PetEntity qwerPet2 = new PetEntity("고양이", 3.2, "하양이", 2024, "암컷", "페르시안", LocalDate.now(), qwer);
        qwer.getPets().add(qwerPet2);
        
        UserEntity asdf = new UserEntity(null, "asdf", passwordEncoder.encode("1234"), "asdf", roles, "asdf123@pawple.com", "010-8888-9999", null, null, null, LocalDateTime.now(), null, new ArrayList<>(),0);

        PetEntity asdfPet1 = new PetEntity("고양이", 2.8, "미미", 2022, "암컷", "러시안블루", LocalDate.now(), asdf);
        asdf.getPets().add(asdfPet1);
        PetEntity asdfPet2 = new PetEntity("강아지", 6.0, "콩이", 2023, "수컷", "말티즈", LocalDate.now(), asdf);
        asdf.getPets().add(asdfPet2);

        userRep.save(qwer);
        userRep.save(asdf);
        petRep.saveAll(List.of(qwerPet1, qwerPet2, asdfPet1, asdfPet2));

        ConsultEntity consult1 = ConsultEntity.builder()
            .title("초코가 자꾸 긁어요")
            .content("최근 초코가 자주 몸을 긁고 있어요. 알러지일까요?")
            .createdAt(LocalDateTime.now().minusDays(4))
            .status("ANSWERED")
            .subCategory("피부")
            .user(qwer)
            .pet(qwerPet1)
            .replyContent("가려움이 지속된다면 피부염일 수 있습니다. 병원 방문을 권장합니다.")
            .replyAuthor(vet.getName())
            .replyCreatedAt(LocalDateTime.now().minusDays(2))
            .build();

        ConsultEntity consult2 = ConsultEntity.builder()
            .title("하양이가 물을 너무 많이 마셔요")
            .content("며칠 전부터 물을 자주 마시는 것 같은데 문제일까요?")
            .createdAt(LocalDateTime.now().minusDays(3))
            .status("PENDING")
            .subCategory("내분비")
            .user(qwer)
            .pet(qwerPet2)
            .build();

        ConsultEntity consult3 = ConsultEntity.builder()
            .title("미미가 자꾸 구토를 해요")
            .content("먹고 바로 토하거나 공복에도 구토 증상이 있어요.")
            .createdAt(LocalDateTime.now().minusDays(5))
            .status("ANSWERED")
            .subCategory("소화기")
            .user(asdf)
            .pet(asdfPet1)
            .replyContent("잦은 구토는 위염이나 헤어볼 때문일 수 있습니다. 진료를 받아보세요.")
            .replyAuthor(vet.getName())
            .replyCreatedAt(LocalDateTime.now().minusDays(4))
            .build();

        ConsultEntity consult4 = ConsultEntity.builder()
            .title("콩이가 산책 중에 자꾸 멈춰요")
            .content("산책하다가 중간에 멈추고 안 가려고 하는데 무슨 이유일까요?")
            .createdAt(LocalDateTime.now().minusDays(2))
            .status("PENDING")
            .subCategory("행동")
            .user(asdf)
            .pet(asdfPet2)
            .build();

        consultRep.saveAll(List.of(consult1, consult2, consult3, consult4));


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
                new ArrayList<>(),
                1
            );

            PetEntity pet1 = new PetEntity(
                "강아지",
                5.0 + i % 3,
                "댕댕이" + suffix,
                2020 + i % 5,
                (i % 2 == 0) ? "수컷" : "암컷",
                "푸들",
                LocalDate.now().minusYears(1 + i % 3),
                user
            );

            PetEntity pet2 = new PetEntity(
                "고양이",
                4.0 + i % 2,
                "냐옹이" + suffix,
                2021 + i % 4,
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
        List<String> subCategories = List.of("홈케어", "식이관리", "병원", "영양제", "행동", "질병");

        int postCountPerCategory = 5;
        int userIndex = 0;

        for (String subCategory : subCategories) {
            for (int i = 1; i <= postCountPerCategory; i++) {
                UserEntity user = userList.get(userIndex % userList.size());
                userIndex++;

                int randomViewCount = ThreadLocalRandom.current().nextInt(1, 101);
                LocalDateTime createdAt = LocalDate.of(2025, 6, 1)
                    .atTime(ThreadLocalRandom.current().nextInt(24), ThreadLocalRandom.current().nextInt(60));

                PostEntity post = PostEntity.builder()
                    .title(user.getName() + "의 [" + subCategory + "] 관련 게시글입니다.")
                    .content("이 글은 " + subCategory + " 주제로 테스트용으로 생성되었습니다.")
                    .category("토픽")
                    .subCategory(subCategory)
                    .user(user)
                    .viewCount(randomViewCount)
                    .createdAt(createdAt)
                    .build();

                postList.add(post);
            }
        }

        for (int i = 0; i < userList.size(); i++) {
            UserEntity user = userList.get(i);
            String suffix = String.format("%02d", i + 1);

            int randomViewCount2 = ThreadLocalRandom.current().nextInt(1, 101);

            LocalDateTime createdAt2 = LocalDate.of(2025, 6, 1)
                .atTime(ThreadLocalRandom.current().nextInt(24), ThreadLocalRandom.current().nextInt(60));

            PostEntity post2 = PostEntity.builder()
                .title("user" + suffix + "의 두 번째 게시글입니다. 게시글 테스트 진행중입니다.")
                .content("user" + suffix + "의 두 번째 게시글입니다.")
                .category("일상")
                .user(user)
                .viewCount(randomViewCount2)
                .createdAt(createdAt2)
                .build();

            postList.add(post2);
        }

        postRep.saveAll(postList);


        PostEntity qwerPost1 = PostEntity.builder()
            .title("강아지 산책 시간이 부족한가요?")
            .content("초코가 요즘 자꾸 짖어서 산책 시간이 부족한지 고민입니다.")
            .category("Q&A")
            .user(qwer)
            .viewCount(102)
            .commentCount(0)
            .build();

        PostEntity qwerPost2 = PostEntity.builder()
            .title("고양이 발톱 관리 팁 공유")
            .content("하양이 발톱을 자를 때 유용한 팁을 공유해요!")
            .category("토픽")
            .subCategory("홈케어")
            .user(qwer)
            .viewCount(165)
            .commentCount(1)
            .build();


        PostEntity qwerPost3 = PostEntity.builder()
            .title("초코가 밥을 안 먹어요 ㅠㅠ")
            .content("요 며칠 초코가 사료를 안 먹는데 병원을 가야 할까요?")
            .category("Q&A")
            .user(qwer)
            .viewCount(110)
            .commentCount(2)
            .build();

        PostEntity qwerPost4 = PostEntity.builder()
            .title("하양이 장난감 추천해요!")
            .content("요즘 하양이가 너무 심심해해요. 잘 놀았던 장난감 공유합니다 :)")
            .category("토픽")
            .subCategory("행동")
            .user(qwer)
            .viewCount(195)
            .commentCount(3)
            .build();

        PostEntity asdfPost1 = PostEntity.builder()
            .title("러시안블루 털 빠짐 심한가요?")
            .content("미미가 털을 많이 뿜어서 관리가 어렵네요. 다들 어떻게 하세요?")
            .category("Q&A")
            .user(asdf)
            .viewCount(89)
            .commentCount(0)
            .build();

        PostEntity asdfPost2 = PostEntity.builder()
            .title("말티즈 목욕 시기 어떻게 정하세요?")
            .content("콩이가 목욕을 싫어해서 고민입니다. 목욕 주기를 어떻게 정하면 좋을까요?")
            .category("토픽")
            .subCategory("홈케어")
            .user(asdf)
            .viewCount(134)
            .commentCount(1)
            .build();

        PostEntity asdfPost3 = PostEntity.builder()
            .title("미미가 자꾸 숨는데 왜 그럴까요?")
            .content("러시안블루 미미가 사람을 피하고 숨어요. 원인을 모르겠어요.")
            .category("Q&A")
            .user(asdf)
            .viewCount(77)
            .commentCount(1)
            .build();

        PostEntity asdfPost4 = PostEntity.builder()
            .title("콩이랑 여행 다녀왔어요! 후기 공유")
            .content("말티즈 콩이와 함께 1박 2일 펫캉스 다녀왔습니다. 팁 공유해요~")
            .category("일상")
            .user(asdf)
            .viewCount(210)
            .commentCount(2)
            .build();

        userRep.save(qwer);
        userRep.save(asdf);

        postRep.save(qwerPost1);
        postRep.save(qwerPost2);
        postRep.save(qwerPost3);
        postRep.save(qwerPost4);
        postRep.save(asdfPost1);
        postRep.save(asdfPost2);
        postRep.save(asdfPost3);
        postRep.save(asdfPost4);

        UserEntity user01 = userList.get(0); // index 0 → user01
        UserEntity user02 = userList.get(1); // index 1 → user02

        CommentEntity comment1 = CommentEntity.builder()
            .content("첫 글 축하드립니다!")
            .user(user01)
            .post(abcdPost1)
            .build();

        CommentEntity comment2 = CommentEntity.builder()
            .content("답변 기다리고 있어요")
            .user(user02)
            .post(abcdPost2)
            .build();

        CommentEntity comment3 = CommentEntity.builder()
            .content("저도 걱정돼요")
            .user(user01)
            .post(abcdPost3)
            .build();

        CommentEntity comment4 = CommentEntity.builder()
            .content("정말 좋은 정보네요")
            .user(user02)
            .post(abcdPost4)
            .build();

        commentRep.saveAll(List.of(comment1, comment2, comment3, comment4));

        // userRep.save(new UserEntity(null, "root", passwordEncoder.encode("1234"), "root", List.of("ADMIN"), null, null, null, LocalDateTime.now(), null, null));
        // userRep.save(new UserEntity(null, "abcd", passwordEncoder.encode("1234"), "abcd", List.of("USER"), null, null, null, LocalDateTime.now(), null, null));
    
        if (productRepository.count() == 0) { // DB가 비어 있을 때만 삽입
            ProductEntity p1 = new ProductEntity(null, "액티베이트 스몰 60p (종합 영양제)", "벳플러스", 4.9, 224, 17, 64900, 78000, List.of("무료배송", "BEST", "특가"), "/images/aktivait.jpg");
            ProductEntity p2 = new ProductEntity(null, "프리미엄 비프(소 단일) (100gx10팩)", "바프독", 5.0, 3, 4, 54900, 57000, List.of("BEST"), "/images/synoquin.jpg");
            // ... 나머지 상품도 동일하게 생성

            productRepository.saveAll(List.of(p1, p2 /*, ... */));
        }
    
    
    
    }


}
