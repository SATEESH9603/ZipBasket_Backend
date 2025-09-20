package com.example.onlinetest.Domain.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.onlinetest.Domain.Dto.ForgotPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<UserRegisterResponseDto> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        UserRegisterResponseDto userResponse = new UserRegisterResponseDto();
        userResponse.error.message = ex.getMessage();
        userResponse.error.errorCode = "USER_ALREADY_EXISTS";
        userResponse.error.developerMessage = java.util.Arrays.toString(ex.getStackTrace());
        return new ResponseEntity<>(userResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserRegisterResponseDto> handleGenericException(Exception ex) {
        UserRegisterResponseDto userResponse = new UserRegisterResponseDto();
        userResponse.error.message = "An error occurred: " + ex.getMessage();
        userResponse.error.errorCode = "INTERNAL_SERVER_ERROR";
        userResponse.error.developerMessage = java.util.Arrays.toString(ex.getStackTrace());
        return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserLoginResponseDto> handleUserNotFoundException(Exception ex) {
        UserLoginResponseDto userResponse = new UserLoginResponseDto();
        userResponse.error.message = "An error occurred: " + ex.getMessage();
        userResponse.error.errorCode = "USER_NOT_FOUND";
        userResponse.error.developerMessage = java.util.Arrays.toString(ex.getStackTrace());
        return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);
    }

    // For profile update context  
    @ExceptionHandler(ProfileUserNotFoundException.class)
    public ResponseEntity<UpdateUserProfileResponseDto> handleProfileUserNotFoundException(ProfileUserNotFoundException ex) {
        UpdateUserProfileResponseDto ProfileUserResponse = new UpdateUserProfileResponseDto();
        ProfileUserResponse.setError(new com.example.onlinetest.Domain.ErrorModel(){{
            setMessage("An error occurred: " + ex.getMessage());
            setErrorCode(HttpStatus.NOT_FOUND.toString());
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return new ResponseEntity<>(ProfileUserResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForgotPasswordUserNotFoundException.class)
    public ResponseEntity<ForgotPasswordResponseDto> handleUserNotFound(ForgotPasswordUserNotFoundException ex) {
        ForgotPasswordResponseDto response = new ForgotPasswordResponseDto();
        response.setSuccess(false);
        response.setError(new com.example.onlinetest.Domain.ErrorModel(){{
            setMessage("User not found: " + ex.getMessage());
            setErrorCode("USER_NOT_FOUND");
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}