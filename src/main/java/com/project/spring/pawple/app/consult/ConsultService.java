package com.project.spring.pawple.app.consult;

import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.pawple.app.pet.PetEntity;
import com.project.spring.pawple.app.pet.PetRepository;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

@Service
public class ConsultService {

    private static final List<String> VALID_SUBCATEGORIES = Arrays.asList(
        "종합 관리", "간담낭", "감염", "구강", "근골격", "내분비", "뇌신경",
        "면역매개", "비뇨기", "생식기", "소화기", "심혈관", "안구", "종양", "피부", "호흡기", "기타"
    );

    private static final List<String> VALID_STATUSES = Arrays.asList(
        "PENDING", "ANSWERED"
    );

    @Autowired
    private ConsultRepository consultRep;

    @Autowired
    private UserRepository userRep;

    @Autowired
    private PetRepository petRep;

    @Transactional
    public ConsultDto createConsultPost(ConsultDto dto, String username) {
        if (!VALID_SUBCATEGORIES.contains(dto.getSubCategory())) {
            throw new IllegalArgumentException("유효하지 않은 상담 주제입니다: " + dto.getSubCategory());
        }

        String status = dto.getStatus() != null ? dto.getStatus().toUpperCase() : "대기";
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("유효하지 않은 상태 값입니다: " + status);
        }

        UserEntity user = userRep.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        PetEntity pet = petRep.findById(dto.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("반려동물을 찾을 수 없습니다."));

        ConsultEntity post = dto.toEntity(user, pet);
        ConsultEntity saved = consultRep.save(post);
        return ConsultDto.fromEntity(saved);
    }


    @Transactional(readOnly = true)
    public ConsultDto getConsultPost(Long id) {
        ConsultEntity post = consultRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상담글을 찾을 수 없습니다."));
        return ConsultDto.fromEntity(post);
    }

    @Transactional(readOnly = true)
    public Page<ConsultDto> getAllConsultPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return consultRep.findAllByOrderByCreatedAtDesc(pageable)
                .map(ConsultDto::fromEntity);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        String upper = status.toUpperCase();
        if (!VALID_STATUSES.contains(upper)) {
            throw new IllegalArgumentException("유효하지 않은 상태입니다: " + status);
        }

        ConsultEntity post = consultRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상담글을 찾을 수 없습니다."));
        post.setStatus(upper);
        consultRep.save(post);
    }

    @Transactional
    public void deleteConsultPost(Long id, String username) {
        ConsultEntity post = consultRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상담글을 찾을 수 없습니다."));
        if (!post.getUser().getName().equals(username)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        consultRep.delete(post);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableCategories() {
        return VALID_SUBCATEGORIES;
    }

    public Page<ConsultDto> getConsultsFiltered(int page, int size, String status, String category) {
        Pageable pageable = PageRequest.of(page, size);

        // 상태와 카테고리 둘 다 있을 경우
        if (status != null && category != null) {
            return consultRep.findByStatusAndSubCategoryOrderByCreatedAtDesc(status, category, pageable)
                    .map(ConsultDto::fromEntity);
        }

        if (status != null) {
            return consultRep.findByStatusOrderByCreatedAtDesc(status, pageable)
                    .map(ConsultDto::fromEntity);
        }

        if (category != null) {
            return consultRep.findBySubCategoryOrderByCreatedAtDesc(category, pageable)
                    .map(ConsultDto::fromEntity);
        }

        // 둘 다 없음
        return consultRep.findAllByOrderByCreatedAtDesc(pageable)
                .map(ConsultDto::fromEntity);
    }

    public Page<ConsultDto> getMyConsults(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ConsultEntity> entities = consultRep.findByUserName(username, pageable);
        return entities.map(ConsultDto::fromEntity);
    }


}
