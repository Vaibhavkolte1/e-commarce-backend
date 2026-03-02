package com.college.e_commarce.controller;

import com.college.e_commarce.dto.LoginRequestDto;
import com.college.e_commarce.dto.RegisterResponseDto;
import com.college.e_commarce.dto.RegisterRequestDto;
import com.college.e_commarce.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public RegisterResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        return authService.getMe();
    }

    @PostMapping("/login")
    public ResponseEntity<RegisterResponseDto> loginUser(@Valid @RequestBody LoginRequestDto dto, HttpServletResponse response) {
        RegisterResponseDto registerResponseDto = authService.loginUser(dto);

        ResponseCookie cookie = ResponseCookie.from("jwt", registerResponseDto.getJwt())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        registerResponseDto.setJwt(null);

        return ResponseEntity.ok(registerResponseDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("User LogOut successfully.");
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(authService.registerUser(dto));
    }
}
