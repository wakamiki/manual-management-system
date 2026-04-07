package com.example.manual.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CategoryRequestDto {

@NotBlank
@Size(max=50)
private String categoryName;

@NotNull
private Integer displayOrder;

}
