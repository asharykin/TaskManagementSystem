package org.effectivemobile.tms.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effectivemobile.tms.util.enums.Priority;

@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequestDto {

    @Schema(description = "Название", example = "Создать новую задачу")
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 32, message = "Длина названия не может превышать 32 символа")
    private String title;

    @Schema(description = "Описание", example = "Использовать данное API для создания новой задачи")
    @Size(max = 1024, message = "Длина описания не может превышать 1024 символа")
    private String description;

    @Schema(description = "Приоритет", example = "LOW/MEDIUM/HIGH")
    @NotNull(message = "Приоритет должен быть выбран")
    private Priority priority;

    @Schema(description = "ID исполнителя", example = "2")
    @NotNull(message = "Исполнитель должен быть выбран")
    @Positive(message = "ID исполнителя должен быть положительным")
    private Long executorId;
}
