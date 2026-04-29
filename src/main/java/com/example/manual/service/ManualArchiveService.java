package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;
import com.example.manual.repository.ManualRepository;

@Service
public class ManualArchiveService {
    private static final Logger log = LoggerFactory.getLogger(ManualCommandService.class);
    private final ManualRepository manualRepository;
    private final ManualHistoryService historyService;

    public ManualArchiveService(
            ManualRepository manualRepository,
            ManualHistoryService manualHistoryService) {

        this.manualRepository = manualRepository;
        this.historyService = manualHistoryService;
    }

    // ==============================================
    // 停止中同名カテゴリー所属マニュアル全アーカイブ処理
    // ==============================================

    public void archiveManualsByInactiveDuplicateCategory(Category category, Principal principal) {

        List<Manual> manuals = manualRepository.findByCategoryAndStatusNot(
                category,
                ManualStatus.ARCHIVED);
        for (Manual manual : manuals) {
            manual.archive();
            manual.markUpdatedNow();
            manualRepository.save(manual);
            String changeNote = "同名カテゴリー追加のためARCHIVE化:更新履歴自動生成";
            historyService.createHistory(manual, changeNote, principal);
        }

    }

}
