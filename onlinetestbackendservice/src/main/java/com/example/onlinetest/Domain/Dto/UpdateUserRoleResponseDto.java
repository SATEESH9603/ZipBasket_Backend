package com.example.onlinetest.Domain.Dto;

import com.example.onlinetest.Domain.ErrorModel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for update user role operation")
public class UpdateUserRoleResponseDto {
    private boolean success;
    private String message;
    private ErrorModel error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorModel getError() {
        return error;
    }

    public void setError(ErrorModel error) {
        this.error = error;
    }
}
