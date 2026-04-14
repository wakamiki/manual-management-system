package com.example.manual.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Notification;



public interface NotificationRepository extends JpaRepository<Notification, Long>{
}

