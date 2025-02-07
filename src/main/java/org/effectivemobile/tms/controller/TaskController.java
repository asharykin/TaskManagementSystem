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
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.effectivemobile.tms.dto.task.TaskCreateRequestDto;
import org.effectivemobile.tms.dto.task.TaskResponseDto;
import org.effectivemobile.tms.dto.task.TaskUpdateRequestDto;
import org.effectivemobile.tms.dto.task.TaskUpdateStatusRequestDto;
import org.effectivemobile.tms.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Задачи")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получить список своих задач (для исполнителей), всех задач с возможностью фильтрации по исполнителю и автору (для администраторов)",
            parameters = {
                    @Parameter(name = "page", description = "Номер страницы для выборки"),
                    @Parameter(name = "size", description = "Количество задач на странице"),
                    @Parameter(name = "executorId", description = "ID исполнителя"),
                    @Parameter(name = "authorId", description = "ID автора")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class))))
            })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                             @RequestParam(name = "executorId", required = false) Long executorId,
                                                             @RequestParam(name = "authorId", required = false) Long authorId) {
        List<TaskResponseDto> taskPage = taskService.getAll(page, size, executorId, authorId);
        return new ResponseEntity<>(taskPage, HttpStatus.OK);
    }

    @Operation(summary = "Получить сведения о своей задаче (для исполнителей), о любой задаче (для администраторов)",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID задачи"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
            })
    @GetMapping(path = "/{id}")
    public ResponseEntity<TaskResponseDto> getById(@PathVariable("id") @Positive Long id) {
        TaskResponseDto task = taskService.getById(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(summary = "Создать новую задачу (только для администраторов)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
            })
    @PostMapping
    public ResponseEntity<TaskResponseDto> create(@RequestBody @Valid TaskCreateRequestDto dto) {
        TaskResponseDto task = taskService.create(dto);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(summary = "Изменить статус своей задачи (для исполнителей), любой задачи (для администраторов)",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID задачи"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
            })
    @PatchMapping(path = "/{id}")
    public ResponseEntity<TaskResponseDto> updateStatus(@PathVariable("id") @Positive Long id, @RequestBody @Valid TaskUpdateStatusRequestDto dto) {
        TaskResponseDto task = taskService.updateStatus(id, dto);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(summary = "Редактировать любую задачу (только для администраторов)",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID задачи"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class)))
            })
    @PutMapping(path = "/{id}")
    public ResponseEntity<TaskResponseDto> update(@PathVariable("id") @Positive Long id, @RequestBody @Valid TaskUpdateRequestDto dto) {
        TaskResponseDto task = taskService.update(id, dto);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(summary = "Удалить любую задачу (только для администраторов)",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID задачи"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(defaultValue = "Задача успешно удалена")))
            })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") @Positive Long id) {
        taskService.delete(id);
        return new ResponseEntity<>("Задача успешно удалена", HttpStatus.OK);
    }
}
