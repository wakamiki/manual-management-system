package com.example.manual.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.entity.User;
import com.example.manual.entity.UserOperationHistory;
import com.example.manual.repository.UserOperationHistoryRepository;

@Service
public class UserOperationHistoryService {
        private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

        private final UserOperationHistoryRepository operationRepository;

        public UserOperationHistoryService(
                        UserOperationHistoryRepository operationRepository) {
                this.operationRepository = operationRepository;
        }

        public void recordCreateUser(
                        User targetUser,
                        User playUser) {
                UserOperationHistory operationHistory = getOperationHistory(targetUser, playUser);
                operationHistory.markCreateUser();
                operationHistory.setOperationDetail(
                                "ID: " + targetUser.getId() + "のユーザーを新規作成しました。");
                operationRepository.save(operationHistory);
        }

        public void recordUpdateUser(
                        User targetUser,
                        User playUser) {
                UserOperationHistory operationHistory = getOperationHistory(targetUser, playUser);
                operationHistory.markUpdateUser();
                operationHistory.setOperationDetail(
                                "ID: " + targetUser.getId() + "のユーザーを更新しました。");
                operationRepository.save(operationHistory);
        }

        public void recordDeactiveteUser(
                        User targetUser,
                        User playUser) {
                UserOperationHistory operationHistory = getOperationHistory(targetUser, playUser);
                operationHistory.markDeactiveteUser();
                operationHistory.setOperationDetail(
                                "ID: " + targetUser.getId() + "のユーザーを停止しました。");
                operationRepository.save(operationHistory);
        }

        public void recordActivateUser(
                        User targetUser,
                        User playUser) {
                UserOperationHistory operationHistory = getOperationHistory(targetUser, playUser);
                operationHistory.markActivateUser();
                operationHistory.setOperationDetail(
                                "ID: " + targetUser.getId() + "のユーザーを復帰しました。");
                operationRepository.save(operationHistory);
        }

        public void recordResetPassword(
                        User targetUser,
                        User playUser) {
                UserOperationHistory operationHistory = getOperationHistory(targetUser, playUser);
                operationHistory.markResetPassword();
                operationHistory.setOperationDetail(
                                "ID: " + targetUser.getId() + "のユーザーパスワードを初期化しました。");
                operationRepository.save(operationHistory);
        }

        public void recordChangePassword(
                        User targetUser,
                        User playUser) {
                UserOperationHistory operationHistory = getOperationHistory(targetUser, playUser);
                operationHistory.markChangePassword();

                operationHistory.setOperationDetail(
                                "ID: " + targetUser.getId() + "のユーザーのパスワードを変更しました。");
                operationRepository.save(operationHistory);
        }

        // =======================================
        // 共通処理
        // =======================================

        public UserOperationHistory getOperationHistory(
                        User targetUser,
                        User playUser) {

                UserOperationHistory operationHistory = new UserOperationHistory();
                operationHistory.setTargetUser(targetUser);
                operationHistory.setOperatedByUser(playUser);
                operationHistory.markCreatedNow();
                return operationHistory;
        }
}
