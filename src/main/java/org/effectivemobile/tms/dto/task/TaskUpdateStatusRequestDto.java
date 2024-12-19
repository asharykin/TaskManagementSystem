package org.effectivemobile.tms.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effectivemobile.tms.util.enums.Status;

@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateStatusRequestDto {

    @Schema(description = "Статус", example = "WAITING/IN_PROGRESS/COMPLETED")
    @NotNull(message = "Статус должен быть выбран")
    private Status status;
}
