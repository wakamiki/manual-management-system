package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;

public interface ManualRepository extends JpaRepository<Manual, Long> 
, JpaSpecificationExecutor<Manual> {

  //荳隕ｧ蜿門ｾ暦ｼ域峩譁ｰ縺梧眠縺励＞繧ゅ・鬆・ｼ・
  List<Manual> findAllByOrderByUpdatedAtDesc();

  // 讀懃ｴ｢繝ｯ繝ｼ繝峨′蜷ｫ縺ｾ繧後ｋ荳隕ｧ蜿門ｾ暦ｼ域峩譁ｰ縺梧眠縺励＞繧ゅ・鬆・ｼ・
  List<Manual> findByTitleContainingOrderByUpdatedAtDesc(String keyword);

  //status邨槭ｊ霎ｼ縺ｿ讀懃ｴ｢讖溯・・域峩譁ｰ縺梧眠縺励＞繧ゅ・鬆・ｼ・
  List<Manual> findByStatusOrderByUpdatedAtDesc(ManualStatus status);

  
}


  //#region この分け方をすると分かりやすい
  //#endregion
