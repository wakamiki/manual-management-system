package com.example.manual.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponseDto {
    
private Long id;

private String categoryName;

private Integer displayOrder;

private boolean isActive;

}
