package com.example.manual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
Optional<User>findByLoginId(String id);

}


  //#region ‚±‚Ì•ª‚¯•û‚ð‚·‚é‚Æ•ª‚©‚è‚â‚·‚¢
  //#endregion
