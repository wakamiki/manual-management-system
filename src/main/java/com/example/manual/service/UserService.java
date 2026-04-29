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
import com.example.manual.dto.UserResponseDto;
import com.example.manual.dto.UserViewDto;
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
    public UserViewDto showUserManagementPage(
            Principal principal, Pageable pageable) {

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
        UserViewDto viewDto = new UserViewDto();
        viewDto.setPagingDto(pagingDto);
        viewDto.setAllUserDto(userResponseDto);
        viewDto.setPlayUser(toCreatedUserDto(playUser));
        List<UserRole> roleList = Arrays.asList(UserRole.values());
        viewDto.setAllRole(roleList);
        viewDto.setUserCount(getAllUserCount());
        viewDto.setCanGuest(userPermission.isGuest(playUser));
        viewDto.setMode(ViewMode.CREATE);
        return viewDto;
    }

    public UserViewDto showUserUpdateMode(
            Principal principal, Long userId, Pageable pageable) {

        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canShowUpdateMode(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        UserViewDto viewDto = showUserManagementPage(principal, pageable);
        viewDto.setMode(ViewMode.EDIT);

        return viewDto;
    }

    public boolean showChangePasswordPage(Principal principal) {

        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canShowChangePasswordPage(playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        if (playUser.getRole() == UserRole.GUEST) {
            return true;
        }
        return false;
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
            UserFormDto formDto,
            Principal principal) {

        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canCreateUser(formDto, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        User targetUser = User.createNew(
                formDto.getLoginId(),
                formDto.getDisplayName(),
                formDto.getRole());

        // パスワードエンコード
        String newPassword = generateInitialPassword();
        targetUser = newCreatePassword(targetUser, newPassword);
        User savedUser = userRepository.save(targetUser);
        operation.recordCreateUser(savedUser, playUser);
        return newPassword;
    }

    public boolean updateUser(
            UserFormDto formDto,
            Principal principal,
            Long id) {

        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canUpdateUser(formDto, playUser)) {
            throw new InvalidStateException("判定エラー");
        }
        // ログインID重複チェック
        if (userPermission.isUserIdTaken(formDto)) {
            return true;
        } // TODO:変更事項があるかチェック
        User targetUser = getUserById(id);
        targetUser.changeLoginId(formDto.getLoginId());
        targetUser.changeRole(formDto.getRole());
        targetUser.setDisplayName(formDto.getDisplayName());
        targetUser.markUpdatedNow();
        User savedUser = userRepository.save(targetUser);
        operation.recordUpdateUser(savedUser, playUser);
        return false;
    }

    public void deactivateUser(
            Principal principal,
            UserFormDto formDto,
            Long id) {

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

        User targetUser = getUserById(id);
        User playUser = getUserByPrincipal(principal);
        if (!userPermission.canResetPassword(playUser, targetUser)) {
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

        Optional<User> userOpt = userRepository.findByLoginId(principal.getName());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser;
    }

    public String getDisplayNameByLoginId(String loginId) {

        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser.getDisplayName();
    }

    public Page<User> findAllUser(Pageable pageable) {

        Page<User> userList = userRepository.findAll(pageable);
        return userList;
    }

    public User getUserByLoginId(String loginId) {

        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User targetUser = userOpt.get();
        return targetUser;
    }

    public User getUserById(Long id) {

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

    private String generateInitialPassword() {

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
    public UserViewDto concertToUserViewDto(
            UserFormDto formDto,
            Principal principal, Pageable pageable) {
        UserViewDto viewDto = showUserManagementPage(principal, pageable);
        if (formDto.getId() == null) {
            viewDto.setMode(ViewMode.CREATE);
        } else {
            viewDto.setMode(ViewMode.EDIT);
        }
        return viewDto;
    }

    public UserFormDto toFormData(Long userId) {
        User targetUser = getUserById(userId);
        UserFormDto formDto = new UserFormDto();
        formDto.setId(targetUser.getId());
        formDto.setDisplayName(targetUser.getDisplayName());
        formDto.setLoginId(targetUser.getLoginId());
        formDto.setRole(targetUser.getRole());
        formDto.setIsActive(targetUser.isActive());
        return formDto;
    }

}
