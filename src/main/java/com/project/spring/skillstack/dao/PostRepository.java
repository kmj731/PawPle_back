package com.project.spring.skillstack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.spring.skillstack.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> { }

