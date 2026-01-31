package com.example.onlinetest.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.onlinetest.Domain.Dto.ForgotPasswordRequestDto;
import com.example.onlinetest.Domain.Dto.ForgotPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.ResetPasswordRequestDto;
import com.example.onlinetest.Domain.Dto.ResetPasswordResponseDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserProfileResponseDto;
import com.example.onlinetest.Domain.Exceptions.ForgotPasswordUserNotFoundException;
import com.example.onlinetest.Domain.Exceptions.ProfileUserNotFoundException;
import com.example.onlinetest.Domain.Mapper;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.UserRepo;
import com.example.onlinetest.Service.JwtToken.IJwtService;

@Service
public class UserProfileService implements IUserProfileService {

    private final UserRepo userRepo;
    private final IEmailService emailService;
    private final IJwtService jwtService;

    // Constructor injection for UserRepo
    public UserProfileService(UserRepo userRepo, IEmailService emailService,IJwtService jwtService) {
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    @Override
    public UpdateUserProfileResponseDto updateProfile(String userName, UpdateUserProfileRequestDto requestDto) {
        User existing =
            userRepo.findByUsername(userName)
                    .orElseThrow(() -> new ProfileUserNotFoundException(
                        "User with username " + userName + " does not exist"));
        var savedUserResponse = userRepo.save(Mapper.toUser(existing, requestDto));
        return Mapper.toUpdateUserProfileResponseDto(savedUserResponse);
    }

    @Override
    public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto request) {
        ForgotPasswordResponseDto forgotPasswordResponseDto = new ForgotPasswordResponseDto();
        try {
            Optional<User> userOpt = userRepo.findByUsername(request.getUsername());

            if (userOpt.isEmpty()
                    || (request.getEmail() != null && !userOpt.get().getEmail().equalsIgnoreCase(request.getEmail()))) {
                // Always return success to avoid revealing if user exists
                forgotPasswordResponseDto.setSuccess(true);
                forgotPasswordResponseDto.setMessage("If the user " + request.getUsername() + " exists, a password reset link has been sent to the registered email.");
                return forgotPasswordResponseDto;                
            }
            User user = userOpt.get();

            // Generate token
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepo.save(user);

            // Send email (pseudo-code)
            emailService.sendResetPasswordEmail(user.getEmail(), token);
            forgotPasswordResponseDto.setSuccess(true);
            forgotPasswordResponseDto.setMessage("If the user " + user.getUsername() + " exists, a password reset link has been sent to the registered email.");
            return forgotPasswordResponseDto;
        } catch (Exception e) {
            forgotPasswordResponseDto.setSuccess(false);
            com.example.onlinetest.Domain.ErrorModel errorModel = new com.example.onlinetest.Domain.ErrorModel();
            errorModel.setMessage("An error occurred: " + e.getMessage());
            errorModel.setErrorCode("INTERNAL_SERVER_ERROR");
            errorModel.setDeveloperMessage(java.util.Arrays.toString(e.getStackTrace()));
            forgotPasswordResponseDto.setError(errorModel);
            return forgotPasswordResponseDto;
        }
    }

    @Override
    public ResetPasswordResponseDto resetPassword(String authHeader, String resetToken, ResetPasswordRequestDto request) {
        ResetPasswordResponseDto resetPasswordResponseDto = new ResetPasswordResponseDto();
            if ((authHeader == null || !authHeader.startsWith("Bearer ")) && resetToken == null) {
                resetPasswordResponseDto.setSuccess(false);
                resetPasswordResponseDto.setMessage("No valid token provided");
                resetPasswordResponseDto.setError(new com.example.onlinetest.Domain.ErrorModel(){{
                    setMessage("No valid token provided");
                    setErrorCode("NO_VALID_TOKEN");
                    setDeveloperMessage("Either an Authorization header with a Bearer token or a reset token must be provided.");
                }});
                return resetPasswordResponseDto;
            }

            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                resetPasswordResponseDto.setSuccess(false);
                    resetPasswordResponseDto.setMessage("Passwords do not match");
                    resetPasswordResponseDto.setError(new com.example.onlinetest.Domain.ErrorModel(){{
                        setMessage("Passwords do not match");
                        setErrorCode("PASSWORDS_DO_NOT_MATCH");
                        setDeveloperMessage("The new password and confirm password fields must match.");
                    }});
                return resetPasswordResponseDto;
            }
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String accessToken = authHeader.substring(7);
                return resetPasswordWithAccessToken(accessToken, request);
            } else {
                return resetPasswordWithToken(resetToken, request);
            }
    }

    public ResetPasswordResponseDto resetPasswordWithAccessToken(String accessToken, ResetPasswordRequestDto request) {
        ResetPasswordResponseDto resetPasswordResponseDto = new ResetPasswordResponseDto();
        try {
            // 1. Validate token and extract username
            String username = jwtService.extractUsername(accessToken);
            
            if (username == null) {
                resetPasswordResponseDto.setSuccess(false);
                    resetPasswordResponseDto.setMessage("Invalid access token");
                    resetPasswordResponseDto.setError(new com.example.onlinetest.Domain.ErrorModel(){{
                        setMessage("Invalid access token");
                        setErrorCode("INVALID_ACCESS_TOKEN");
                        setDeveloperMessage("The provided access token is invalid or expired.");
                    }});
                return resetPasswordResponseDto;
            }

            // 2. Fetch user from DB
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. Optional: Check old password if required
            if (request.getNewPassword().equals(user.getPassword())) {
                resetPasswordResponseDto.setSuccess(false);
                resetPasswordResponseDto.setMessage("Old and new password are both same");
                resetPasswordResponseDto.setError(new com.example.onlinetest.Domain.ErrorModel(){{
                    setMessage("Old and new password are both same");
                    setErrorCode("OLD_PASSWORD_INCORRECT");
                    setDeveloperMessage("The provided old password does not match the current password.");
                }});

                return resetPasswordResponseDto;
            }

            // 4. Hash and update new password
            //String hashedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(request.getNewPassword());
            userRepo.save(user);

            // 5. Return success
            resetPasswordResponseDto.setSuccess(true);
            resetPasswordResponseDto.setMessage("Password updated successfully");
            return resetPasswordResponseDto;
        } catch (Exception e) {
            resetPasswordResponseDto.setSuccess(false);
            resetPasswordResponseDto.setMessage("An error occurred: " + e.getMessage());
            resetPasswordResponseDto.setError(new com.example.onlinetest.Domain.ErrorModel(){{
                setMessage("An error occurred: " + e.getMessage());
                setErrorCode("INTERNAL_SERVER_ERROR");
                setDeveloperMessage(java.util.Arrays.toString(e.getStackTrace()));
            }});
            return resetPasswordResponseDto;
        }
    }

    @Override
    public ResetPasswordResponseDto resetPasswordWithToken(String token, ResetPasswordRequestDto request) {
        ResetPasswordResponseDto resetPasswordResponseDto = new ResetPasswordResponseDto();
        try {
            Optional<User> userOpt = userRepo.findByResetToken(token);

            if (userOpt.isEmpty() || userOpt.get().getResetTokenExpiry() == null
                    || userOpt.get().getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                        resetPasswordResponseDto.setSuccess(false);
                        resetPasswordResponseDto.setMessage("Invalid or expired token.");
                        resetPasswordResponseDto.setError(new com.example.onlinetest.Domain.ErrorModel(){{
                            setMessage("Invalid or expired token.");
                            setErrorCode("INVALID_OR_EXPIRED_TOKEN");
                            setDeveloperMessage("The provided reset token is either invalid or has expired.");
                        }});
                        return resetPasswordResponseDto;
            }

            User user = userOpt.get();
            user.setPassword(request.getNewPassword());
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepo.save(user);

            resetPasswordResponseDto.setSuccess(true);
            resetPasswordResponseDto.setMessage("Password has been reset successfully.");
            return resetPasswordResponseDto;
        } catch (ForgotPasswordUserNotFoundException e) {
            throw e; // Rethrow to be handled by global exception handler
        } catch (RuntimeException e) {
            resetPasswordResponseDto.setSuccess(false);
            com.example.onlinetest.Domain.ErrorModel errorModel = new com.example.onlinetest.Domain.ErrorModel();
            errorModel.setMessage("An error occurred: " + e.getMessage());
            errorModel.setErrorCode("INTERNAL_SERVER_ERROR");
            errorModel.setDeveloperMessage(java.util.Arrays.toString(e.getStackTrace()));
            resetPasswordResponseDto.setError(errorModel);
            return resetPasswordResponseDto;
        }
    }

}
