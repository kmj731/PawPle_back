package com.project.spring.skillstack.dao;

import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Long> {
    Optional<PetEntity> findByOwnerAndPetName(UserEntity owner, String petName);
    List<PetEntity> findByOwner(UserEntity owner);
}
