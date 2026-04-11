package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualListDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.service.ManualService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/manuals")
public class ManualController {


private final ManualService manualService;

public ManualController(ManualService manualService) {
    this.manualService = manualService;
    }


@GetMapping
public List<ManualListDto>searchManuals(ManualSearchConditionDto condition){
    List<ManualListDto>manualDtoList = manualService.searchManuals(condition);
    return manualDtoList;
}
//#endregion
    @PostMapping("/{manualId}/actions/submit")
    public String submitManual(@PathVariable Long manualId, Principal principal) {
        manualService.submitManual(manualId);
        return "繝槭ル繝･繧｢繝ｫ繧貞・髢九＠縺ｾ縺励◆縲・;
    }

    @PostMapping("/{manualId}/actions/save-draft")
    public String saveDraftForCreate(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.saveDraftForCreate(manualId,requestDto, principal);
        return "繝槭ル繝･繧｢繝ｫ繧剃ｸ区嶌縺阪↓菫晏ｭ倥＠縺ｾ縺励◆";
    }
    
    @PostMapping("/{manualId}/actions/save-draft-copy")
    public String saveDraftForCopy(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.saveDraftForCopy(manualId,requestDto, principal);
        return "隍・｣ｽ繝槭ル繝･繧｢繝ｫ繧剃ｸ区嶌縺阪↓菫晏ｭ倥＠縺ｾ縺励◆";
    }

    @PostMapping("/{manualId}/actions/submit-pending")
    public String submitToPending(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.submitToPending(manualId, requestDto, principal);
        return "繝槭ル繝･繧｢繝ｫ繧呈価隱榊ｾ・■蜈ｬ髢九＠縺ｾ縺励◆縲・;
    }

    @PostMapping("/{manualId}/actions/edit-to-pending")
    public String editToPending(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.editToPending(manualId, requestDto, principal);
        return "繝槭ル繝･繧｢繝ｫ繧呈価隱榊ｾ・■蜈ｬ髢九＠縺ｾ縺励◆縲・;
    }

//#endregion

    @GetMapping("/{manualId}")
    public ManualDetailDto goToDetailPage(@PathVariable Long manualId) {
        ManualDetailDto detailDto = manualService.goToDetailPage(manualId);
        return detailDto;
    }

    @PostMapping("/{manualId}/actions/copy") 
    public ManualResponseDto goToCopyPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = manualService.goToCopyPage(manualId, principal);
        return responseDto;
     }
    
    @PostMapping("/{manualId}/actions/edit")
    public ManualResponseDto goToEditPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = manualService.goToEditPage(manualId, principal);
        return responseDto;
    }
    
//#endregion
//#region action
    @PostMapping("/{manualId}/actions/approve")
    public String approveManual(@PathVariable Long manualId, Principal principal) {
        manualService.approveManual(manualId, principal);
        return "繝槭ル繝･繧｢繝ｫ繧呈価隱阪＠縺ｾ縺励◆縲・;
    }

    @PostMapping("/{manualId}/actions/approve-with-comment")
    public String approveManualWithComment(@PathVariable Long manualId,@Valid ManualActionRequestDto actionRequestDto, Principal principal) {
        manualService.approveManualWithComment(manualId,actionRequestDto.getChangeNote(),principal);
        return "繝槭ル繝･繧｢繝ｫ繧呈価隱阪＠縺ｾ縺励◆縲・;
    }
    @PostMapping("/{manualId}/actions/rollback")
    public String rollbackEditManual(@PathVariable Long manualId,@Valid ManualActionRequestDto actionRequestDto,Principal principal) {
        manualService.rollbackEditManual(manualId,actionRequestDto.getChangeNote(), principal);
        return "繝槭ル繝･繧｢繝ｫ繧貞ｷｮ縺玲綾縺励∪縺励◆縲・;
    }

    @PostMapping("/{manualId}/actions/archive")
    public String archiveManual(@PathVariable Long manualId,@Valid ManualActionRequestDto actionRequestDto,Principal principal){
        manualService.archiveManual(manualId, actionRequestDto, principal);
        return "繝槭ル繝･繧｢繝ｫ繧偵い繝ｼ繧ｫ繧､繝悶＠縺ｾ縺励◆縲・;
    }
    @PostMapping("/{manualId}/actions/restore")
    public String restoreManual(@PathVariable Long manualId,ManualActionRequestDto actionRequestDto,Principal principal) {
        manualService.restoreManual(manualId,actionRequestDto.getChangeNote(),principal);
        return "繝槭ル繝･繧｢繝ｫ繧偵い繝ｼ繧ｫ繧､繝悶°繧牙ｾｩ蟶ｰ縺励∪縺励◆縲・;
        }
//#endregion
    private String getLoginId(Principal principal) {
        String loginId = principal.getName();
        return loginId;
    }
//#endregion
}
