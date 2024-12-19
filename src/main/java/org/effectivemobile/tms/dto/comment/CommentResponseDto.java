package org.effectivemobile.tms.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    @Schema(description = "ID комментария", example = "1")
    private Long id;

    @Schema(description = "ID задачи", example = "1")
    private Long taskId;

    @Schema(description = "Содержимое", example = "Содержимое комментария")
    private String content;

    @Schema(description = "Автор", example = "1")
    private Long authorId;
}
