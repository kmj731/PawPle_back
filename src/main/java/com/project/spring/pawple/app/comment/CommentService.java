package com.project.spring.pawple.app.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.pawple.app.notification.NotificationService;
import com.project.spring.pawple.app.post.PostEntity;
import com.project.spring.pawple.app.post.PostRepository;
import com.project.spring.pawple.app.user.UserEntity;
import com.project.spring.pawple.app.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentLikeService commentLikeService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService; //  알림 서비스 주입

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        UserEntity user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostEntity post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        CommentEntity parent = null;
        if (commentDto.getParentId() != null) {
            parent = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        CommentEntity comment = CommentEntity.builder()
                .content(commentDto.getContent())
                .user(user)
                .post(post)
                .parent(parent)
                .build();

        //  포인트 적립
        user.addPoint(1);
        userRepository.save(user);

        comment = commentRepository.save(comment);

        //  게시글의 댓글 수 증가
        postRepository.increaseCommentCount(commentDto.getPostId());

        //  알림 전송: 자신이 아닌 경우에만
        UserEntity postAuthor = post.getUser();
        if (!postAuthor.getId().equals(user.getId())) {
            notificationService.notifyPostAuthor(
                    postAuthor,
                    post,
                    user.getName() + "님이 게시글에 댓글을 남겼습니다."
            );
        }

        return mapToDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(commentDto.getUserId())) {
            throw new RuntimeException("You are not authorized to update this comment");
        }

        comment.setContent(commentDto.getContent());
        comment = commentRepository.save(comment);

        return mapToDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);

        //  댓글 수 감소
        postRepository.decreaseCommentCount(postId);
    }

    public CommentDto mapToDto(CommentEntity comment) {
        List<CommentDto> childDtos = comment.getChildren() != null ?
                comment.getChildren().stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
                : new ArrayList<>();

        long likeCount = commentLikeService.getLikeCount(comment.getId());

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .postId(comment.getPost().getId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .children(childDtos)
                .likeCount(likeCount)
                .build();
    }
}
