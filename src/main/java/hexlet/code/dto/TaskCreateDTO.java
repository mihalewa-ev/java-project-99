package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class TaskCreateDTO {


    private long index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String content;

    @NotBlank(message = "Status must not be blank")
    private String status;

    private List<Long> taskLabelIds;
}
