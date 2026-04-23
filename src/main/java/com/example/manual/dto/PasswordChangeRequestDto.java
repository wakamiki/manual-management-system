package com.example.manual.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequestDto {
    public PasswordChangeRequestDto() {

    }

    @NotBlank
    private String currentPassword;
    @NotBlank
    @Size(min = 8, max = 32)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
    @Pattern(regexp = "^[A-Za-z\\d!@#$%^&*()_\\-+=]+$")
    private String newPassword;
    @NotBlank
    private String confirmPassword;

    // getter
    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // setter

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

}
