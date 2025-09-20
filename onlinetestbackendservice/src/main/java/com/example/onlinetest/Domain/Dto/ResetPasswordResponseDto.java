package com.example.onlinetest.Domain.Dto;
import com.example.onlinetest.Domain.ErrorModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordResponseDto {
    private boolean success;
    private ErrorModel error;
    private String message;
}
