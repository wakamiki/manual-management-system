package com.example.manual.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users>findByUserId(Long id);
}
