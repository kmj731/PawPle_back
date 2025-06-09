package com.project.spring.pawple.application.consult;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consult")
public class ConsultController {

    @Autowired
    private ConsultService consultService;
    @Autowired
    private ConsultRepository consultRep;

    // 상담글 등록
    @PostMapping
    public ResponseEntity<ConsultDto> createConsult(
            @Valid @RequestBody ConsultDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = userDetails.getUsername();
        ConsultDto saved = consultService.createConsultPost(dto, username);
        return ResponseEntity.ok(saved);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ConsultDto> getConsult(@PathVariable Long id) {
        ConsultDto dto = consultService.getConsultPost(id);
        return ResponseEntity.ok(dto);
    }

    // 전체 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<ConsultDto>> getConsults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {

        // 전체 조회 또는 필터 조회를 하나의 서비스에서 처리
        Page<ConsultDto> posts = consultService.getConsultsFiltered(page, size, status, category);
        return ResponseEntity.ok(posts);
    }

    // 상태 변경 (예: PENDING → ANSWERED)
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        consultService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsult(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        consultService.deleteConsultPost(id, username);
        return ResponseEntity.ok().build();
    }

    // 답변 등록
    @PutMapping("/{id}/reply")
    public ResponseEntity<?> addReply(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ConsultEntity post = consultRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상담글이 없습니다."));

        if (post.getReplyContent() != null) {
            return ResponseEntity.badRequest().body("이미 답글이 등록된 상담입니다.");
        }

        post.setReplyContent(body.get("content"));
        post.setReplyAuthor(userDetails.getUsername());
        post.setReplyCreatedAt(LocalDateTime.now());
        post.setStatus("ANSWERED");

        consultRep.save(post);
        return ResponseEntity.ok().build();
    }

    // 본인이 작성한 상담글 조회
    @GetMapping("/my")
    public ResponseEntity<Page<ConsultDto>> getMyConsults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = userDetails.getUsername();
        Page<ConsultDto> myPosts = consultService.getMyConsults(username, page, size);
        return ResponseEntity.ok(myPosts);
    }

}
