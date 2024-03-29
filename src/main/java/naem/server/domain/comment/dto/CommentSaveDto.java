package naem.server.domain.comment.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSaveDto {

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
}
