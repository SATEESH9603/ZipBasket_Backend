package com.example.onlinetest.Domain.Dto;
import com.example.onlinetest.Domain.ErrorModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordResponseDto {
    private boolean success;
    private String message;
    private ErrorModel error;

}
