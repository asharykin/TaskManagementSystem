package org.effectivemobile.tms.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class TaskResponseDto {

    @Schema(description = "ID задачи", example = "1")
    private Long id;

    @Schema(description = "Название", example = "Создать новую задачу")
    private String title;

    @Schema(description = "Описание", example = "Использовать данное API для создания новой задачи")
    private String description;

    @Schema(description = "Статус", example = "WAITING/IN_PROGRESS/COMPLETED")
    private Status status;

    @Schema(description = "Приоритет", example = "LOW/MEDIUM/HIGH")
    private Priority priority;

    @Schema(description = "ID автора", example = "1")
    private Long authorId;

    @Schema(description = "ID исполнителя", example = "2")
    private Long executorId;
}
