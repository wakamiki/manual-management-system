package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.manual.dto.UserDetailDto;
import com.example.manual.dto.UserFormDto;
import com.example.manual.dto.UserRequestDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.enums.ViewMode;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =============================================
    // 画面表示
    // =============================================

    // user-management表示
    public UserFormDto showUserManagementPage(
            User playUser) {
        log.info("start");
        if (!canShowUserManagementPage(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        List<User> userList = userRepository.findAllByOrderByUpdatedAtDesc();
        List<UserDetailDto> userResponseDto = new ArrayList<>();
        for (User user : userList) {
            UserDetailDto responseDto = new UserDetailDto();
            responseDto.setId(user.getId());
            responseDto.setLoginId(user.getLoginId());
            responseDto.setDisplayName(user.getDisplayName());
            responseDto.setRole(user.getRole());
            responseDto.setActive(user.isActive());
            responseDto.setLastLoginAt(user.getLastLoginAt());
            responseDto.setActiveLabel(getActivateLabel(user.isActive()));
            userResponseDto.add(responseDto);
        }
        UserFormDto formDto = new UserFormDto();
        formDto.setAllUserDto(userResponseDto);
        formDto.setPlayUser(toCreatedUserDto(playUser));
        List<UserRole> roleList = Arrays.asList(UserRole.values());
        formDto.setAllRole(roleList);
        formDto.setUserCount(getAllUserCount());
        formDto.setMode(ViewMode.CREATE);
        return formDto;
    }

    public UserFormDto showUpdateMode(
            Principal principal, Long userId) {
        User playUser = getUserByPrincipal(principal);
        User targetUser = getUserById(userId);
        if (!canShowUpdateMode(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        UserDetailDto detailDto = toCreatedUserDetailDto(targetUser);
        UserFormDto formDto = showUserManagementPage(playUser);
        formDto.setTargetUser(detailDto);
        formDto.setMode(ViewMode.EDIT);

        return formDto;
    }

    // =============================================
    // 更新・変更処理
    // =============================================

    public UserDetailDto createUser(
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

        UserDetailDto responseDto = new UserDetailDto();
        responseDto.setId(savedUser.getId());
        responseDto.setLoginId(savedUser.getLoginId());
        responseDto.setDisplayName(savedUser.getDisplayName());
        responseDto.setRole(savedUser.getRole());
        responseDto.setActive(savedUser.isActive());
        return responseDto;
    }

    public UserDetailDto updateUser(
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

        UserDetailDto responseDto = new UserDetailDto();
        responseDto.setId(savedUser.getId());
        responseDto.setLoginId(savedUser.getLoginId());
        responseDto.setDisplayName(savedUser.getDisplayName());
        responseDto.setRole(savedUser.getRole());
        responseDto.setActive(savedUser.isActive());
        responseDto.setLastLoginAt(savedUser.getLastLoginAt());
        return responseDto;
    }

    public UserDetailDto changeRole(
            @Valid UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        if (!canChangeRole(requestDto, principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserDetailDto responseDto = new UserDetailDto();
        return responseDto;
    }

    public UserDetailDto deactivateUser(Principal principal) {
        log.info("start");
        if (!canDeactivateUser(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserDetailDto responseDto = new UserDetailDto();
        return responseDto;
    }

    public UserDetailDto activateUser(Principal principal) {
        log.info("start");
        if (!canActivateUser(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserDetailDto responseDto = new UserDetailDto();
        return responseDto;
    }

    public UserDetailDto resetPassword(Principal principal) {
        log.info("start");
        if (!canResetPassword(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        UserDetailDto responseDto = new UserDetailDto();
        return responseDto;
    }

    public User userSaved(User user, Principal principal) {
        log.info("start");
        if (!canUserSaved(user, principal)) {
            throw new InvalidStateException("判定エラー");
        }
        return userRepository.save(user);
    }

    // =============================================
    // 取得・検索系
    // =============================================

    // ユーザー管理画面取得
    public List<UserDetailDto> getUserManagementViewData(Principal principal) {
        log.info("start");
        if (!canGetUserManagementViewData(principal)) {
            throw new InvalidStateException("判定エラー");
        }
        List<User> allUsers = findAllUser();
        List<UserDetailDto> allUserDto = toUserListDtoList(allUsers);
        return allUserDto;
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

    public User getUserByLoginId(String loginId) {
        log.info("start");
        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser;
    }

    public User getUserById(Long id) {
        log.info("start");
        Optional<User> userOpt = userRepository.findById(id);
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

    private Long getAllUserCount() {
        Long count = userRepository.count();
        return count;
    }

    private String getActivateLabel(boolean isActive) {
        if (isActive) {
            String label = "使用中";
            return label;
        } else {
            String label = "停止中";
            return label;
        }
    }

    // =========================================
    // Dto詰替
    // =========================================

    // 画面表示用にDto変換
    public List<UserDetailDto> toUserListDtoList(List<User> userList) {
        log.info("start");
        List<UserDetailDto> userListDto = new ArrayList<>();
        for (User targetUser : userList) {
            UserDetailDto userDto = new UserDetailDto();
            userDto.setLoginId(
                    targetUser.getLoginId());
            userDto.setDisplayName(
                    targetUser.getDisplayName() != null
                            ? targetUser.getDisplayName()
                            : "");
            userDto.setRole(
                    targetUser.getRole());
            userDto.setLastLoginAt(targetUser.getLastLoginAt());
            userDto.setActive(targetUser.isActive());
            userListDto.add(userDto);
        }

        return userListDto;
    }

    // index表示用にDto変換
    public UserResponseDto toUserIndexViewDto(Principal principal) {
        User targetUser = getUserByPrincipal(principal);
        UserResponseDto userDto = new UserResponseDto();
        userDto.setDisplayName(
                targetUser.getDisplayName() != null
                        ? targetUser.getDisplayName()
                        : "");
        userDto.setId(targetUser.getId());
        userDto.setUserRole(targetUser.getRole());
        return userDto;
    }

    // 検索一覧画面表示用にDto変換
    public UserResponseDto toCreatedUserDto(User targetUser) {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setDisplayName(
                targetUser.getDisplayName() != null
                        ? targetUser.getDisplayName()
                        : "");
        userDto.setId(targetUser.getId());
        userDto.setUserRole(targetUser.getRole());
        return userDto;
    }

    public UserDetailDto toCreatedUserDetailDto(User targetUser) {
        UserDetailDto userDto = new UserDetailDto();
        userDto.setId(targetUser.getId());
        userDto.setLoginId(targetUser.getLoginId());
        userDto.setDisplayName(targetUser.getDisplayName());
        userDto.setRole(targetUser.getRole());
        userDto.setActive(targetUser.isActive());
        userDto.setActiveLabel(getActivateLabel(targetUser.isActive()));
        return userDto;
    }

    // =========================================
    // 権限判定
    // =========================================

    private boolean canShowUserManagementPage(User targetUser) {
        log.info("start");
        if (!targetUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (targetUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        return true;
    }

    private boolean canShowUpdateMode(User targetUser) {
        log.info("start");
        if (!targetUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (targetUser.getRole() != UserRole.ADMIN) {
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
        for (User targetUser : users) {
            if (requestDto.getLoginId().equals(targetUser.getLoginId())) {
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

    private boolean canGetUserManagementViewData(
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
