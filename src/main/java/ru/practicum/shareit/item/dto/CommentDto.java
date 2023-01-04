package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CommentDto {
    private long id;
    @NotBlank(groups = Create.class, message = "Comment text is blank")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
