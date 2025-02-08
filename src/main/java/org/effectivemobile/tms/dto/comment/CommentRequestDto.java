package org.effectivemobile.tms.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @Schema(description = "Комментарий", example = "Содержимое комментария")
    @NotBlank(message = "Содержимое комментария не может быть пустым")
    @Size(max = 256, message = "Длина комментария не может превышать 256 символов")
    private String content;

}
