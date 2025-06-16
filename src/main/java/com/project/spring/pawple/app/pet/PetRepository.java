package com.project.spring.pawple.app.pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.spring.pawple.app.user.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PetRepository extends JpaRepository<PetEntity, Long> {
    Optional<PetEntity> findByOwnerAndPetName(UserEntity owner, String petName);
    List<PetEntity> findByOwner(UserEntity owner);
    List<PetEntity> findByOwnerId(Long ownerId);

    @Modifying
    @Query("delete from PetEntity p where p.owner.id = :ownerId")
    void deleteByOwnerId(@Param("ownerId") Long ownerId);

    

}
