package com.college.e_commarce.service.impl;

import com.college.e_commarce.dto.LoginRequestDto;
import com.college.e_commarce.dto.RegisterResponseDto;
import com.college.e_commarce.dto.RegisterRequestDto;
import com.college.e_commarce.entity.Cart;
import com.college.e_commarce.entity.User;
import com.college.e_commarce.enums.Role;
import com.college.e_commarce.enums.UserStatus;
import com.college.e_commarce.repository.CartRepository;
import com.college.e_commarce.repository.UserRepository;
import com.college.e_commarce.service.AuthService;
import com.college.e_commarce.service.JwtService;
import com.college.e_commarce.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtService jwtService;
    private final AuthUtil authUtil;

    @Override
    public RegisterResponseDto getMe() {
        User me = authUtil.getCurrentUser();
        return RegisterResponseDto.builder()
                .id(me.getId())
                .name(me.getName())
                .email(me.getEmail())
                .address(me.getAddress())
                .role(me.getRole())
                .build();
    }

    @Override
    public RegisterResponseDto loginUser(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if(user.getActive() == UserStatus.BLOCKED) {
            throw new RuntimeException("User Blocked");
        }

        return RegisterResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .role(user.getRole())
                .jwt(jwtService.generateToken(user))
                .build();
    }

    @Transactional
    @Override
    public RegisterResponseDto registerUser(RegisterRequestDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Role assignRole;
        assert dto.getRole() != null;
        if(dto.getRole().equals("SELLER")) {
            assignRole = Role.SELLER;
        } else {
            assignRole = Role.USER;
        }

        User newUser = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(assignRole)
                .address(dto.getAddress())
                .createdAt(LocalDateTime.now())
                .active(UserStatus.ACTIVE)
                .build();

        Cart newCart = Cart.builder()
                .createdAt(LocalDateTime.now())
                .owner(newUser)
                .build();

        newUser.setCart(newCart);

        userRepository.save(newUser);

        return RegisterResponseDto.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .address(newUser.getAddress())
                .role(newUser.getRole())
                .build();
    }
}
