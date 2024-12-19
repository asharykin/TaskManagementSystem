package org.effectivemobile.tms.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.user.UserAuthRequestDto;
import org.effectivemobile.tms.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Аутентификация")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(defaultValue = "Ваш JWT")))
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<String> signUp(@RequestBody @Valid UserAuthRequestDto dto) {
        String jwt = authService.signUp(dto);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @Operation(summary = "Авторизация пользователя", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(defaultValue = "Ваш JWT")))
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<String> signIn(@RequestBody @Valid UserAuthRequestDto dto) {
        String jwt = authService.signIn(dto);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @Hidden
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException() {
        return new ResponseEntity<>("Неверное имя пользователя или пароль", HttpStatus.BAD_REQUEST);
    }

}
