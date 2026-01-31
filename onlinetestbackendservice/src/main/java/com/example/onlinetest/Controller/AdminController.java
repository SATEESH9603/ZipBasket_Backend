package com.example.onlinetest.Controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlinetest.Domain.Dto.UpdateUserRoleRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateUserRoleResponseDto;
import com.example.onlinetest.Domain.ErrorModel;
import com.example.onlinetest.Repo.UserRepo;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.UserRole;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin-only operations")
public class AdminController {

    private final UserRepo userRepo;

    public AdminController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PutMapping("/users/{userName}/role")
    public ResponseEntity<UpdateUserRoleResponseDto> updateUserRole(
            @PathVariable("userName") String userName,
            @Valid @RequestBody UpdateUserRoleRequestDto req) {

        UpdateUserRoleResponseDto resp = new UpdateUserRoleResponseDto();

        try {
            User user = userRepo.findByUsername(userName).orElse(null);
            if (user == null) {
                resp.setSuccess(false);
                resp.setMessage("User not found");
                resp.setError(new ErrorModel(){{
                    setMessage("User with userName " + userName + " not found");
                    setErrorCode("USER_NOT_FOUND");
                }});
                return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
            }

            // Map requested role to enum
            UserRole newRole;
            try {
                newRole = UserRole.valueOf(req.getRole().toUpperCase());
            } catch (IllegalArgumentException iae) {
                resp.setSuccess(false);
                resp.setMessage("Invalid role");
                resp.setError(new ErrorModel(){{
                    setMessage("Invalid role: " + req.getRole());
                    setErrorCode("INVALID_ROLE");
                }});
                return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
            }

            user.setRole(newRole.name());
            userRepo.save(user);

            resp.setSuccess(true);
            resp.setMessage("User role updated successfully");
            return new ResponseEntity<>(resp, HttpStatus.OK);

        } catch (IllegalArgumentException ex) {
            resp.setSuccess(false);
            resp.setMessage("Invalid user userName");
            resp.setError(new ErrorModel(){{
                setMessage("Invalid UUID: " + ex.getMessage());
                setErrorCode("INVALID_ID");
            }});
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            resp.setSuccess(false);
            resp.setMessage("Failed to update role");
            resp.setError(new ErrorModel(){{
                setMessage(ex.getMessage());
                setErrorCode("UPDATE_ERROR");
            }});
            return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
