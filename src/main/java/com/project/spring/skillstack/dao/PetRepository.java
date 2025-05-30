package com.project.spring.skillstack.dao;

import com.project.spring.skillstack.entity.PetEntity;
import com.project.spring.skillstack.entity.UserEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Long> {
    Optional<PetEntity> findByOwnerAndPetName(UserEntity owner, String petName);
    List<PetEntity> findByOwner(UserEntity owner);


    @Modifying
    @Query("delete from PetEntity p where p.owner.id = :ownerId")
    void deleteByOwnerId(@Param("ownerId") Long ownerId);

}
