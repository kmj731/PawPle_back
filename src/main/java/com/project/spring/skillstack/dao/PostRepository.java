package com.project.spring.skillstack.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.spring.skillstack.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
