package org.effectivemobile.tms.service;

import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.user.UserAuthRequestDto;
import org.effectivemobile.tms.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public String signUp(UserAuthRequestDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        userService.create(dto);
        User user = userService.loadUserByUsername(dto.getUsername());
        return jwtService.generateToken(user);
    }

    public String signIn(UserAuthRequestDto dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        User user = userService.loadUserByUsername(dto.getUsername());
        return jwtService.generateToken(user);
    }
}
