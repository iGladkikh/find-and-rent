package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    private static final ZoneId TIMEZONE_ID = ZoneId.systemDefault();

    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreatedAt(LocalDateTime.from(comment.getCreatedAt().atZone(TIMEZONE_ID)));
        return commentDto;
    }

    public static List<CommentDto> toDto(Iterable<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        comments.forEach(comment -> dtos.add(toDto(comment)));
        return dtos;
    }

    public static Comment toModel(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }
}
