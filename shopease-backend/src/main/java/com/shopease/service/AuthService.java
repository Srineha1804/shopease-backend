package com.shopease.service;

import com.shopease.dto.AuthResponse;
import com.shopease.dto.LoginRequest;
import com.shopease.dto.RegisterRequest;
import com.shopease.entity.Role;
import com.shopease.entity.User;
import com.shopease.repository.RoleRepository;
import com.shopease.repository.UserRepository;
import com.shopease.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(
                request.getPassword()));
        user.setPhone(request.getPhone());

        // Role assignment
        String roleName;
        if (request.getRole() != null) {
            switch (request.getRole().toUpperCase()) {
                case "SELLER" -> roleName = "ROLE_SELLER";
                case "ADMIN" -> roleName = "ROLE_ADMIN";
                default -> roleName = "ROLE_BUYER";
            }
        } else {
            roleName = "ROLE_BUYER";
        }

        Role role = roleRepository.findByName(
                        Role.RoleName.valueOf(roleName))
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Role.RoleName.valueOf(roleName));
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return "User registered successfully!";
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);
        String role = user.getRoles().stream()
                .findFirst()
                .map(r -> r.getName().name())
                .orElse("ROLE_BUYER");

        return new AuthResponse(token, user.getEmail(),
                user.getName(), role);
    }
}