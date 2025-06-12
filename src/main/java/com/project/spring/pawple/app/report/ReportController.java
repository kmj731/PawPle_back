package com.project.spring.pawple.app.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @PostMapping
    public ResponseEntity<?> report(@RequestBody ReportEntity report) {

        if ("POST".equals(report.getTargetType()) && report.getPostId() == null) {
            throw new IllegalArgumentException("게시글 신고에는 postId가 필요합니다.");
        }

        if ("COMMENT".equals(report.getTargetType()) && report.getCommentId() == null) {
            throw new IllegalArgumentException("댓글 신고에는 commentId가 필요합니다.");
        }

        report.setReportedAt(LocalDateTime.now());
        reportRepository.save(report);

System.out.println("신고 접수: reporter=" + report.getReporterId() +
                   ", reported=" + report.getReportedUserId() +
                   ", post=" + report.getPostId() +
                   ", comment=" + report.getCommentId() +
                   ", reason=" + report.getReason());
        return ResponseEntity.ok("신고가 접수되었습니다.");
    }




}

