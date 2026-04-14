package com.example.manual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
Optional<User>findByLoginId(String id);

//Status:admin/approver全取得(特定ユーザーを除く)
List<User> findByRoleInAndIsActiveTrueAndIdNot(UserRole[] roles, Long excludedUserId);

List<User> findAll();
}
