package org.effectivemobile.tms.service;

import jakarta.persistence.EntityExistsException;
import org.effectivemobile.tms.dto.user.UserAuthRequestDto;
import org.effectivemobile.tms.dto.user.UserResponseDto;
import org.effectivemobile.tms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
    }

    @Test
    void signUp_ShouldSucceed_WhenUserIsNew() {
        UserAuthRequestDto requestDto = new UserAuthRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("password");

        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(user.getPassword());
        when(userService.create(any(UserAuthRequestDto.class))).thenReturn(userResponseDto);
        when(userService.loadUserByUsername(requestDto.getUsername())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mockJwt");

        String jwt = authService.signUp(requestDto);

        assertEquals("mockJwt", jwt);
    }


    @Test
    void signUp_ShouldThrowException_WhenUserAlreadyExists() {
        UserAuthRequestDto requestDto = new UserAuthRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("password");

        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn(user.getPassword());
        when(userService.create(any(UserAuthRequestDto.class))).thenThrow(new EntityExistsException("Пользователь уже существует"));

        assertThrows(EntityExistsException.class, () -> authService.signUp(requestDto));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void signIn_ShouldSucceed_WhenCredentialsAreCorrect() {
        UserAuthRequestDto requestDto = new UserAuthRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(userService.loadUserByUsername(requestDto.getUsername())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mockJwt");

        String jwt = authService.signIn(requestDto);

        assertEquals("mockJwt", jwt);
    }

    @Test
    void signIn_ShouldThrowException_WhenPasswordIsIncorrect() {
        UserAuthRequestDto requestDto = new UserAuthRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Неверный логин или пароль"));

        assertThrows(BadCredentialsException.class, () -> authService.signIn(requestDto));
    }

}
