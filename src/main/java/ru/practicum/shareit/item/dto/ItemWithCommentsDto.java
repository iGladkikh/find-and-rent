package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemWithCommentsDto extends ItemDto {
    private List<CommentDto> comments;
}
