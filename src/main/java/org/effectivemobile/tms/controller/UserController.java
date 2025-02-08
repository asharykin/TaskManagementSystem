package org.effectivemobile.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.user.UserResponseDto;
import org.effectivemobile.tms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Пользователи")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить список всех пользователей (только для администраторов)",
            parameters = {
                    @Parameter(name = "page", description = "Номер страницы"),
                    @Parameter(name = "size", description = "Количество пользователей на странице")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))))
            })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<UserResponseDto> users = userService.getAll(page, size);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Получить сведения о себе (для пользователей), о любом пользователе (для администраторов)",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID пользователя"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
            })
    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable("id") Long id) {
        UserResponseDto user = userService.getById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
