package com.example.onlinetest.Domain.Dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
    @NotBlank(message = "token is required")
    private String token;
    @NotBlank(message = "newPassword is required")
    private String newPassword;
    @NotBlank(message = "confirmPassword is required")
    private String confirmPassword;
}
