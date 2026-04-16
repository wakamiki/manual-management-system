package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.manual.dto.UserListResponseDto;
import com.example.manual.dto.UserRequestDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // user-management表示
    public List<UserResponseDto> showUserManagementPege(
            Principal principal) {
        log.info("start");
                User targetUser = getUserByPrincipal(principal);
        if (!canShowUserManagementPege(targetUser)) {
            throw new InvalidStateException("判定エラー");
        }
        List<User> userList =
                userRepository.findAllByOrderByUpdatedAtDesc();
        List<UserResponseDto> userResponseDto = new ArrayList<>();
        for (User user : userList) {
            UserResponseDto responseDto = new UserResponseDto();
            responseDto.setLoginId(user.getLoginId());
            responseDto.setDisplayName(user.getDisplayName());
            responseDto.setRole(user.getRole());
            responseDto.setActive(user.isActive());
            responseDto.setLastLoginAt(user.getLastLoginAt());
            userResponseDto.add(responseDto);
        }
        return userResponseDto;
    }

    public UserResponseDto createUser(
            UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
                if (!canCreateUser(requestDto, principal)) {
            throw new InvalidStateException("判定エラー");
        }
        User targetUser = new User();
        targetUser.createNew(
                requestDto.getLoginId(),
                requestDto.getDisplayName(),
                requestDto.getRole());

        User savedUser = userRepository.save(targetUser);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setLoginId(savedUser.getLoginId());
        responseDto.setDisplayName(savedUser.getDisplayName());
        responseDto.setRole(savedUser.getRole());
        responseDto.setActive(savedUser.isActive());
        return responseDto;
    }

    public UserResponseDto updateUser(
            @Valid UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        if (!canUpdateUser(requestDto, principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserRole role = requestDto.getRole();

        User targetUser = new User();
        targetUser.applyRole(role);
        targetUser.setDisplayName(requestDto.getDisplayName());
        targetUser.markUpdatedNow();
        User savedUser = userRepository.save(targetUser);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setLoginId(savedUser.getLoginId());
        responseDto.setDisplayName(savedUser.getDisplayName());
        responseDto.setRole(savedUser.getRole());
        responseDto.setActive(savedUser.isActive());
        responseDto.setLastLoginAt(savedUser.getLastLoginAt());
        return responseDto;
    }

    public UserResponseDto changeRole(
            @Valid UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        if (!canChangeRole(requestDto, principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserResponseDto responseDto = new UserResponseDto();
        return responseDto;
    }

    public UserResponseDto deactivateUser(Principal principal) {
        log.info("start");
        if (!canDeactivateUser(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserResponseDto responseDto = new UserResponseDto();
        return responseDto;
    }

    public UserResponseDto activateUser(Principal principal) {
        log.info("start");
        if (!canActivateUser(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserResponseDto responseDto = new UserResponseDto();
        return responseDto;
    }

    public UserResponseDto resetPassword(Principal principal) {
        log.info("start");
        if (!canResetPassword(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserResponseDto responseDto = new UserResponseDto();
        return responseDto;
    }

    public User userSaved(User user, Principal principal) {
        log.info("start");
        if (!canUserSaved(user, principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserResponseDto responseDto = new UserResponseDto();
        return userRepository.save(user);
    }

    // =============================================
    // 取得・検索系
    // =============================================

    // ユーザー管理画面取得
    public UserListResponseDto getUserManagementViewDeta(Principal principal) {
        log.info("start");
        if (!canGetUserManagementViewDeta(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        List<User> allUsers = findAllUser();
        List<UserResponseDto> allUserDto = toUserListDtoList(allUsers);
        UserListResponseDto userListResponseDto = new UserListResponseDto();
        return userListResponseDto;
    }

    public User getUserByPrincipal(Principal principal) {
        log.info("start");
        Optional<User> userOpt = userRepository.findByLoginId(principal.getName());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser;
    }

    public String getDisplayNameByLoginId(String loginId) {
        log.info("start");
        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser.getDisplayName();
    }

    public List<User> findAllUser() {
        log.info("start");
        List<User> userList = userRepository.findAll();
        return userList;
    }

    public User getUserByloginId(String loginId) {
        log.info("start");
        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser;
    }

    // Status:admin/approver全取得(特定ユーザーを除く)
    public List<User> findApproverAndAdminUsersExcept(Long excludedUserId) {
        log.info("start");
        UserRole[] roles = { UserRole.ADMIN, UserRole.APPROVER };
        List<User> users = userRepository.findByRoleInAndIsActiveTrueAndIdNot(roles, excludedUserId);
        return users;
    }


    // =========================================
    // Dto詰替
    // =========================================

    // 画面表示用にDto変換
    public List<UserResponseDto> toUserListDtoList(List<User> userList) {
        log.info("start");
        List<UserResponseDto> userListDto = new ArrayList<>();
        for (User targetUser : userList) {
            UserResponseDto userDto = new UserResponseDto();
            userDto.setLoginId(targetUser.getLoginId());
            userDto.setDisplayName(targetUser.getDisplayName());
            userDto.setRole(targetUser.getRole());
            userDto.setLastLoginAt(targetUser.getLastLoginAt());
            userDto.setActive(targetUser.isActive());
            userListDto.add(userDto);
        }

        return userListDto;
    }

    // =========================================
    // 権限判定
    // =========================================

    private boolean canShowUserManagementPege(User user) {
        log.info("start");
        if (!user.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        return true;
    }

    private boolean canCreateUser(
            UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        List<User> users = userRepository.findAll();
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (playUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        for (User tergetUser : users) {
            if (requestDto.getLoginId().equals(tergetUser.getLoginId())) {
                throw new InvalidStateException(
                        "すでに同じユーザーIDが使われています。");
            }
        }
        return true;
    }

    private boolean canUpdateUser(
            UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canChangeRole(
            UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
                User playUser = getUserByPrincipal(principal);
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canDeactivateUser(Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canActivateUser(Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canResetPassword(Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canUserSaved(
            User user,
            Principal principal) {
        log.info("start");
                User playUser = getUserByPrincipal(principal);
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canGetUserManagementViewDeta(
            Principal principal) {
        log.info("start");
                User targetUser = getUserByPrincipal(principal);
        if (!targetUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (targetUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        return true;
    }
}
