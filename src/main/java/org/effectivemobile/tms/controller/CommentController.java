package org.effectivemobile.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.comment.CommentRequestDto;
import org.effectivemobile.tms.dto.comment.CommentResponseDto;
import org.effectivemobile.tms.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Комментарии к задачам")
@RestController
@RequestMapping("/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Получить список комментариев к своей задаче (для исполнителей), к любой задаче (для администраторов)",
            parameters = {
                    @Parameter(name = "taskId", in = ParameterIn.PATH, description = "ID задачи"),
                    @Parameter(name = "page", description = "Номер страницы"),
                    @Parameter(name = "size", description = "Количество комментариев на странице")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class))))
            })
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getAll(@PathVariable("taskId") Long taskId,
                                                           @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<CommentResponseDto> comments = commentService.getAll(taskId, page, size);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Добавить новый комментарий к своей задаче (для исполнителей), к любой задаче (для администраторов)",
            parameters = @Parameter(name = "taskId", in = ParameterIn.PATH, description = "ID задачи"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = CommentResponseDto.class)))
            })
    @PostMapping
    public ResponseEntity<CommentResponseDto> create(@PathVariable("taskId") Long taskId, @RequestBody @Valid CommentRequestDto dto) {
        CommentResponseDto comment = commentService.create(taskId, dto);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

}