package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.dto.UserDetailDto;
import com.example.manual.dto.UserFormDto;
import com.example.manual.dto.UserRequestDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.enums.ViewMode;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.UserRepository;

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
            Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
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
        UserFormDto formDto = showUserManagementPage(principal);
        formDto.setTargetUser(detailDto);
        formDto.setMode(ViewMode.EDIT);

        return formDto;
    }

    // =============================================
    // 更新・変更処理
    // =============================================

    public void updateLastLoginAt(String loginId) {
        User playUser = getUserByLoginId(loginId);
        if (!canUpdateLastLoginAt(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        playUser.markLastLoginNow();
        userRepository.save(playUser);
    }

    // TODO: 監査ログ追加予定
    public boolean createUser(
            UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!canCreateUser(requestDto, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        // 重複チェック
        if (isUserIdTaken(requestDto)) {
            return true;
        }
        User targetUser = User.createNew(
                requestDto.getLoginId(),
                requestDto.getDisplayName(),
                requestDto.getRole());

        userRepository.save(targetUser);
        return false;
    }

    // TODO: 監査ログ追加予定
    public boolean updateUser(
            UserRequestDto requestDto,
            Principal principal,
            Long id) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!canUpdateUser(requestDto, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        // ログインID重複チェック
        if (isUserIdTaken(requestDto)) {
            return true;
        }
        User targetUser = getUserById(id);
        targetUser.changeLoginId(requestDto.getLoginId());
        targetUser.changeRole(requestDto.getRole());
        targetUser.setDisplayName(requestDto.getDisplayName());
        targetUser.markUpdatedNow();
        userRepository.save(targetUser);
        return false;
    }

    // TODO: 監査ログ追加予定
    public void deactivateUser(
            Principal principal,
            UserRequestDto requestDto,
            Long id) {
        log.info("start");
        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!canDeactivateUser(targetUser, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        targetUser.deactivate();
        targetUser.markUpdatedNow();
        userRepository.save(targetUser);
    }

    // TODO: 監査ログ追加予定
    public void activateUser(
            Principal principal,
            Long id) {
        log.info("start");
        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!canActivateUser(targetUser, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        targetUser.activate();
        targetUser.markUpdatedNow();
        userRepository.save(targetUser);
    }

    // TODO: 監査ログ追加予定
    public String resetPassword(
            Principal principal,
            Long id) {
        log.info("start");
        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!canResetPassword(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        // 処理記入
        String newPassword = "newpass";
        targetUser.setPassword("newpass");
        targetUser.markUpdatedNow();
        userRepository.save(targetUser);
        return newPassword;
    }

    // =============================================
    // 取得・検索系
    // =============================================

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

    private String encodePassword(String raw) {
        return raw; // 今は平文、後で passwordEncoder.encode(raw) に差し替え
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

    // 重複チェック時に値を返すためDTO詰め替え
    public UserFormDto concertToUserFormDto(
            UserRequestDto requestDto,
            Principal principal) {
        UserFormDto formDto = showUserManagementPage(principal);
        UserDetailDto detailDto = new UserDetailDto();
        detailDto.setId(requestDto.getId());
        detailDto.setLoginId(requestDto.getLoginId());
        detailDto.setDisplayName(requestDto.getDisplayName());
        detailDto.setRole(requestDto.getRole());
        formDto.setTargetUser(detailDto);
        if (requestDto.getId() == null) {
            formDto.setMode(ViewMode.CREATE);
        } else {
            formDto.setMode(ViewMode.EDIT);
        }
        return formDto;
    }

    // =========================================
    // 権限判定
    // =========================================

    private boolean canUpdateLastLoginAt(User playUser) {
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

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
            User playUser) {
        log.info("start");

        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (playUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (requestDto.getLoginId() == null || requestDto.getLoginId().isBlank()) {
            throw new NotFoundException("userIdは必須入力項目です。");
        }
        if (requestDto.getDisplayName() == null || requestDto.getDisplayName().isBlank()) {
            throw new NotFoundException("user名は必須入力項目です。");
        }
        if (requestDto.getRole() == null) {
            throw new NotFoundException("Roleは必須入力項目です。");
        }
        return true;
    }

    private boolean canUpdateUser(
            UserRequestDto requestDto,
            User playUser) {
        log.info("start");
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (playUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        return true;
    }

    private boolean canDeactivateUser(User targetUser, User playUser) {
        log.info("start");
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (playUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (!targetUser.isActive()) {
            throw new InvalidStateException("対象のユーザーは既に停止中です。");
        }
        return true;
    }

    private boolean canActivateUser(User targetUser, User playUser) {
        log.info("start");
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (playUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (targetUser.isActive()) {
            throw new InvalidStateException("対象のユーザーは既に有効になっています。");
        }
        return true;
    }

    private boolean canResetPassword(User playUser) {
        log.info("start");
        if (!playUser.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (playUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        return true;
    }

    public boolean isUserIdTaken(UserRequestDto requestDto) {
        log.info("start");
        if (requestDto.getId() == null) {
            return userRepository.existsByLoginId(requestDto.getLoginId());
        }
        return userRepository.existsByLoginIdAndIdNot(requestDto.getLoginId(), requestDto.getId());
    }

}
