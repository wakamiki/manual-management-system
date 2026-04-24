package com.example.manual.service;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.dto.PagingDto;
import com.example.manual.dto.PasswordChangeRequestDto;
import com.example.manual.dto.UserDetailDto;
import com.example.manual.dto.UserFormDto;
import com.example.manual.dto.UserRequestDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.enums.ViewMode;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.repository.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserPermissionService userPermission;
    private final UserOperationHistoryService operation;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserPermissionService userPermission,
            UserOperationHistoryService operation) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPermission = userPermission;
        this.operation = operation;
    }

    // =============================================
    // 画面表示
    // =============================================

    // user-management表示
    public UserFormDto showUserManagementPage(
            Principal principal, Pageable pageable) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canShowUserManagementPage(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        Page<User> userList = userRepository.findAllByOrderByUpdatedAtDesc(pageable);
        List<UserDetailDto> userResponseDto = new ArrayList<>();
        PagingDto pagingDto = PagingDto.from(userList);
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
        formDto.setPagingDto(pagingDto);
        formDto.setAllUserDto(userResponseDto);
        formDto.setPlayUser(toCreatedUserDto(playUser));
        List<UserRole> roleList = Arrays.asList(UserRole.values());
        formDto.setAllRole(roleList);
        formDto.setUserCount(getAllUserCount());
        formDto.setMode(ViewMode.CREATE);
        return formDto;
    }

    public UserFormDto showUserUpdateMode(
            Principal principal, Long userId, Pageable pageable) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        User targetUser = getUserById(userId);
        if (!userPermission.canShowUpdateMode(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        UserDetailDto detailDto = toCreatedUserDetailDto(targetUser);
        UserFormDto formDto = showUserManagementPage(principal, pageable);
        formDto.setTargetUser(detailDto);
        formDto.setMode(ViewMode.EDIT);

        return formDto;
    }

    public void showChangePasswordPage(Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canShowChangePasswordPage(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
    }

    // =============================================
    // 更新・変更処理
    // =============================================

    public void updateLastLoginAt(String loginId) {
        User playUser = getUserByLoginId(loginId);
        if (!userPermission.canUpdateLastLoginAt(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        playUser.markLastLoginNow();
        userRepository.save(playUser);
    }

    public String createUser(
            UserRequestDto requestDto,
            Principal principal) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canCreateUser(requestDto, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        User targetUser = User.createNew(
                requestDto.getLoginId(),
                requestDto.getDisplayName(),
                requestDto.getRole());

        // パスワードエンコード
        String newPassword = generateInitialPassword();
        targetUser = newCreatePassword(targetUser, newPassword);
        User savedUser = userRepository.save(targetUser);
        operation.recordCreateUser(savedUser, playUser);
        return newPassword;
    }

    public boolean updateUser(
            UserRequestDto requestDto,
            Principal principal,
            Long id) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canUpdateUser(requestDto, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        // ログインID重複チェック
        if (userPermission.isUserIdTaken(requestDto)) {
            return true;
        } // TODO:変更事項があるかチェック
        User targetUser = getUserById(id);
        targetUser.changeLoginId(requestDto.getLoginId());
        targetUser.changeRole(requestDto.getRole());
        targetUser.setDisplayName(requestDto.getDisplayName());
        targetUser.markUpdatedNow();
        User savedUser = userRepository.save(targetUser);
        operation.recordUpdateUser(savedUser, playUser);
        return false;
    }

    public void deactivateUser(
            Principal principal,
            UserRequestDto requestDto,
            Long id) {
        log.info("start");
        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canDeactivateUser(targetUser, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        targetUser.deactivate();
        targetUser.markUpdatedNow();
        User savedUser = userRepository.save(targetUser);
        operation.recordDeactiveteUser(savedUser, playUser);
    }

    public void activateUser(
            Principal principal,
            Long id) {
        log.info("start");
        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canActivateUser(targetUser, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        targetUser.activate();
        targetUser.markUpdatedNow();
        User savedUser = userRepository.save(targetUser);
        operation.recordActivateUser(savedUser, playUser);
    }

    public String resetPassword(
            Principal principal,
            Long id) {
        log.info("start");
        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canResetPassword(playUser)) {
            throw new InvalidStateException("判定エラー");
        }

        targetUser.markUpdatedNow();
        String newPassword = generateInitialPassword();
        targetUser = newCreatePassword(targetUser, newPassword);
        User savedUser = userRepository.save(targetUser);
        operation.recordResetPassword(savedUser, playUser);
        return newPassword;
    }

    public void changePassword(
            Principal principal,
            PasswordChangeRequestDto passwordDto) {
        log.info("start");
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canChangePassword(playUser, passwordDto)) {
            throw new InvalidStateException("判定エラー");
        }
        playUser.markUpdatedNow();
        playUser = encodePassword(passwordDto.getNewPassword(), playUser);
        // 要変更フラグ削除
        playUser.clearPasswordChangeRequired();
        User savedUser = userRepository.save(playUser);
        operation.recordChangePassword(savedUser, playUser);

    }

    private User newCreatePassword(User targetUser, String newPassword) {
        log.info("start");
        String encodePassword = passwordEncoder.encode(newPassword);
        targetUser.setPassword(encodePassword);
        targetUser.markPasswordChangeRequired();

        return targetUser;
    }

    private User encodePassword(String newPassword, User targetUser) {
        String encodePassword = passwordEncoder.encode(newPassword);
        targetUser.setPassword(encodePassword);
        return targetUser;
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

    public Page<User> findAllUser(Pageable pageable) {
        log.info("start");
        Page<User> userList = userRepository.findAll(pageable);
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
        log.info("start");
        Long count = userRepository.count();
        return count;
    }

    private String getActivateLabel(boolean isActive) {
        log.info("start");
        if (isActive) {
            String label = "使用中";
            return label;
        } else {
            String label = "停止中";
            return label;
        }
    }

    private String generateInitialPassword() {
        log.info("start");
        String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String lower = "abcdefghijkmnopqrstuvwxyz";
        String digit = "23456789";
        String symbol = "!@#$&";

        String all = upper + lower + digit + symbol;

        SecureRandom random = new SecureRandom();
        List<Character> password = new ArrayList<>();

        // 必須
        password.add(upper.charAt(random.nextInt(upper.length())));
        password.add(lower.charAt(random.nextInt(lower.length())));
        password.add(digit.charAt(random.nextInt(digit.length())));

        for (int i = 0; i < 9; i++) {
            password.add(all.charAt(random.nextInt(all.length())));
        }

        // シャッフル
        Collections.shuffle(password);

        StringBuilder result = new StringBuilder();
        for (char c : password) {
            result.append(c);
        }
        return result.toString();
    }

    // =========================================
    // Dto詰替
    // =========================================

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
            Principal principal, Pageable pageable) {
        UserFormDto formDto = showUserManagementPage(principal, pageable);
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

}
