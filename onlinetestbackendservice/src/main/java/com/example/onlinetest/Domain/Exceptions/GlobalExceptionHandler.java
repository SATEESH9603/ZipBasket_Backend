package com.example.onlinetest.Domain.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.example.onlinetest.Domain.Dto.CreateProductResponseDto;
import com.example.onlinetest.Domain.Dto.ForgotPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Domain.Dto.UserLoginResponseDto;
import com.example.onlinetest.Domain.Dto.UserRegisterResponseDto;
import com.example.onlinetest.Domain.Dto.CartUpdateResponseDto;
import com.example.onlinetest.Domain.Dto.CheckoutResponseDto;
import com.example.onlinetest.Domain.ErrorModel;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<UserRegisterResponseDto> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        UserRegisterResponseDto userResponse = new UserRegisterResponseDto();
        if (userResponse.getError() == null) {
            userResponse.setError(new ErrorModel());
        }
        userResponse.getError().setMessage(ex.getMessage());
        userResponse.getError().setErrorCode("USER_ALREADY_EXISTS");
        userResponse.getError().setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(userResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserRegisterResponseDto> handleGenericException(Exception ex) {
        UserRegisterResponseDto userResponse = new UserRegisterResponseDto();
        if (userResponse.getError() == null) {
            userResponse.setError(new ErrorModel());
        }
        userResponse.getError().setMessage("An error occurred: " + ex.getMessage());
        userResponse.getError().setErrorCode("INTERNAL_SERVER_ERROR");
        userResponse.getError().setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(userResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserLoginResponseDto> handleUserNotFoundException(Exception ex) {
        UserLoginResponseDto userResponse = new UserLoginResponseDto();
        if (userResponse.getError() == null) {
            userResponse.setError(new ErrorModel());
        }
        userResponse.getError().setMessage("An error occurred: " + ex.getMessage());
        userResponse.getError().setErrorCode("USER_NOT_FOUND");
        userResponse.getError().setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);
    }

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

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ProductsListResponseDto> handleProductException(ProductException ex) {
        ProductsListResponseDto response = new ProductsListResponseDto();
        response.setSuccess(false);
        response.setMessage("Error processing products request");
        response.setError(new com.example.onlinetest.Domain.ErrorModel(){{
            setMessage(ex.getMessage());
            setErrorCode("PRODUCT_ERROR");
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Cart-specific exceptions
    @ExceptionHandler(CartException.class)
    public ResponseEntity<CartUpdateResponseDto> handleCartException(CartException ex) {
        CartUpdateResponseDto resp = new CartUpdateResponseDto();
        resp.setSuccess(false);
        resp.setMessage("Cart operation failed: " + ex.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<CheckoutResponseDto> handleOrderException(OrderException ex) {
        CheckoutResponseDto resp = new CheckoutResponseDto();
        resp.setSuccess(false);
        resp.setMessage("Order operation failed: " + ex.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CreateProductResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        CreateProductResponseDto resp = new CreateProductResponseDto();
        resp.setSuccess(false);
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .findFirst().orElse(ex.getMessage());
        resp.setMessage("Validation failed: " + message);
        resp.setError(new com.example.onlinetest.Domain.ErrorModel(){{
            setMessage(message);
            setErrorCode("INVALID_INPUT");
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CreateProductResponseDto> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        CreateProductResponseDto resp = new CreateProductResponseDto();
        resp.setSuccess(false);
        resp.setMessage("Malformed JSON request");
        resp.setError(new com.example.onlinetest.Domain.ErrorModel(){{
            setMessage("Malformed JSON request: " + ex.getMessage());
            setErrorCode("MALFORMED_JSON");
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CreateProductResponseDto> handleAccessDenied(AccessDeniedException ex) {
        CreateProductResponseDto resp = new CreateProductResponseDto();
        resp.setSuccess(false);
        resp.setMessage("Forbidden: insufficient permissions");
        resp.setError(new ErrorModel(){{
            setMessage("Forbidden: " + ex.getMessage());
            setErrorCode("FORBIDDEN");
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CreateProductResponseDto> handleAuthenticationException(AuthenticationException ex) {
        CreateProductResponseDto resp = new CreateProductResponseDto();
        resp.setSuccess(false);
        resp.setMessage("Unauthorized: authentication failed");
        resp.setError(new ErrorModel(){{
            setMessage("Unauthorized: " + ex.getMessage());
            setErrorCode("UNAUTHORIZED");
            setDeveloperMessage(java.util.Arrays.toString(ex.getStackTrace()));
        }});
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
    }

}