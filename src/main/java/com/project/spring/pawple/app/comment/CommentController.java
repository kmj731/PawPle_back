package com.project.spring.pawple.app.comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final UserRepository userRepository;

    private UserEntity getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        return userRepository.findByName(auth.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        UserEntity user = getAuthenticatedUser();
        commentDto.setUserId(user.getId());
        CommentDto createdComment = commentService.createComment(commentDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        try {
            UserEntity user = getAuthenticatedUser();
            commentDto.setUserId(user.getId());
            CommentDto updatedComment = commentService.updateComment(commentId, commentDto);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            UserEntity user = getAuthenticatedUser();
            commentService.deleteComment(commentId, user.getId());
            return ResponseEntity.ok(Map.of("message", "댓글이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long commentId) {
        try {
            UserEntity user = getAuthenticatedUser();
            boolean isLiked = commentLikeService.toggleLike(commentId, user);
            long likeCount = commentLikeService.getLikeCount(commentId);
            return ResponseEntity.ok(Map.of(
                    "isLiked", isLiked,
                    "likeCount", likeCount
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{commentId}/like/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        long likeCount = commentLikeService.getLikeCount(commentId);
        boolean isLiked = false;

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            UserEntity user = userRepository.findByName(username).orElse(null);
            if (user != null) {
                isLiked = commentLikeService.isLikedByUser(commentId, user);
            }
        }

        return ResponseEntity.ok(Map.of(
                "isLiked", isLiked,
                "likeCount", likeCount
        ));
    }

    @GetMapping("/mentionable")
    public ResponseEntity<List<MentionUserDto>> getMentionableUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser"))
                ? auth.getName()
                : "";

        List<MentionUserDto> mentionable = userRepository.findAll().stream()
                .filter(user -> !user.getName().equals(currentUsername))
                .map(user -> new MentionUserDto(user.getId(), user.getSocialName()))
                .toList();

        return ResponseEntity.ok(mentionable);
    }
}
