package com.example.manual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Manual;

public interface ManualRepository extends JpaRepository<Manual, Long> {
}
