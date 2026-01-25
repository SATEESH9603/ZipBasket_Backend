package com.example.onlinetest.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.onlinetest.Domain.Dto.UserLoginRequestDto;
import com.example.onlinetest.Domain.Dto.UserRegisterRequestDto;
import com.example.onlinetest.Domain.Exceptions.UserAlreadyExistException;
import com.example.onlinetest.Domain.Exceptions.UserNotFoundException;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.UserRepo;
import com.example.onlinetest.Service.JwtToken.IJwtService;

class AuthenticationServiceTest {

    @Test
    void login_userNotFound_throws_andDoesNotSave() {
        UserRepo userRepo = mock(UserRepo.class);
        IJwtService jwtService = mock(IJwtService.class);

        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());

        AuthenticationService service = new AuthenticationService(userRepo, jwtService);

        UserLoginRequestDto req = new UserLoginRequestDto();
        req.setUsername("alice");
        req.setPassword("pw");

        assertThrows(UserNotFoundException.class, () -> service.login(req));
        verify(userRepo, never()).save(any());
    }

    @Test
    void login_failedIncrementsLoginAttempts() {
        UserRepo userRepo = mock(UserRepo.class);
        IJwtService jwtService = mock(IJwtService.class);

        User u = new User();
        u.setUsername("alice");
        u.setPassword("correct");
        u.setLoginAttempts(2);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));

        AuthenticationService service = new AuthenticationService(userRepo, jwtService);

        UserLoginRequestDto req = new UserLoginRequestDto();
        req.setUsername("alice");
        req.setPassword("wrong");

        assertThrows(UserNotFoundException.class, () -> service.login(req));
        verify(userRepo).save(argThat(saved -> saved.getLoginAttempts() == 3));
    }

    @Test
    void login_successResetsLoginAttemptsAndSetsLastLogin() {
        UserRepo userRepo = mock(UserRepo.class);
        IJwtService jwtService = mock(IJwtService.class);

        User u = new User();
        u.setUsername("alice");
        u.setPassword("correct");
        u.setLoginAttempts(5);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));
        when(jwtService.generateToken(u)).thenReturn("token");

        AuthenticationService service = new AuthenticationService(userRepo, jwtService);

        UserLoginRequestDto req = new UserLoginRequestDto();
        req.setUsername("alice");
        req.setPassword("correct");

        var resp = service.login(req);
        assertEquals("token", resp.getToken());
        verify(userRepo).save(argThat(saved -> saved.getLoginAttempts() == 0 && saved.getLastLogin() != null));
    }

    @Test
    void register_success_savesUser() {
        UserRepo userRepo = mock(UserRepo.class);
        IJwtService jwtService = mock(IJwtService.class);

        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            // mimic db assignment if needed by mapper
            if (u.getUsername() == null) u.setUsername("alice");
            return u;
        });

        AuthenticationService service = new AuthenticationService(userRepo, jwtService);

        UserRegisterRequestDto req = new UserRegisterRequestDto();
        req.setUsername("alice");
        req.setPassword("pw");
        req.setEmail("alice@example.com");

        var resp = service.register(req);
        assertNotNull(resp);
        verify(userRepo).save(any(User.class));
    }

    @Test
    void register_duplicateUser_throwsUserAlreadyExistException() {
        UserRepo userRepo = mock(UserRepo.class);
        IJwtService jwtService = mock(IJwtService.class);

        User existing = new User();
        existing.setUsername("alice");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(existing));

        AuthenticationService service = new AuthenticationService(userRepo, jwtService);

        UserRegisterRequestDto req = new UserRegisterRequestDto();
        req.setUsername("alice");
        req.setPassword("pw");
        req.setEmail("alice@example.com");

        assertThrows(UserAlreadyExistException.class, () -> service.register(req));
        verify(userRepo, never()).save(any());
    }
}
