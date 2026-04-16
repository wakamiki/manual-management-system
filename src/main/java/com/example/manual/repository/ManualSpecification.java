package com.example.manual.repository;


import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;


public class ManualSpecification {

    public static Specification<Manual> containsKeyword (String keyword){
        if(keyword == null||keyword.isBlank()){
            return null;
        }

        String likeKeyword = "%" + keyword.toLowerCase()+"%";

        return (root,query,cb)-> cb.or(
            cb.like(cb.lower(root.get("title")),likeKeyword),
            cb.like(cb.lower(root.get("content")),likeKeyword)
        );
        }

    public static Specification<Manual> hasCategoryIds(List<Long> categoryIds){
        if (categoryIds==null||categoryIds.isEmpty()) {
            return null;
        }

            return  (root, query, cb) -> {
        var inClause =  cb.in(root.get("category").get("id"));
            for(Long categoryId : categoryIds){
                inClause.value(categoryId);
            }
            return  inClause;
            };
    }

    public static Specification<Manual> hasStatuses(List<ManualStatus> statuses){
        if (statuses==null||statuses.isEmpty()) {
            return null;
        }

        return  (root, query, cb) -> {
        var inClause = cb.in(root.get("status"));
            for (ManualStatus status : statuses) {
            inClause.value(status);
            }
           return inClause;
        };

    }
}
