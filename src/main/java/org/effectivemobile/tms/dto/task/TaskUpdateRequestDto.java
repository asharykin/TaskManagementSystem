package org.effectivemobile.tms.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effectivemobile.tms.util.enums.Priority;
import org.effectivemobile.tms.util.enums.Status;

@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequestDto {

    @Schema(description = "Название", example = "Создать новую задачу")
    @Size(max = 32, message = "Длина названия не может превышать 32 символа")
    private String title;

    @Schema(description = "Описание", example = "Использовать данное API для создания новой задачи")
    @Size(max = 1024, message = "Длина описания не может превышать 1024 символа")
    private String description;

    @Schema(description = "Статус", example = "WAITING/IN_PROGRESS/COMPLETED")
    private Status status;

    @Schema(description = "Приоритет", example = "LOW/MEDIUM/HIGH")
    private Priority priority;

    @Schema(description = "ID исполнителя", example = "2")
    @Positive(message = "ID исполнителя должен быть положительным")
    private Long executorId;
}
