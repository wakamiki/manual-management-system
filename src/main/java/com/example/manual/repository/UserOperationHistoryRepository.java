package com.example.manual.repository;

import com.example.manual.entity.UserOperationHistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOperationHistoryRepository extends JpaRepository<UserOperationHistory, Long> {

}
