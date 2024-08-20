package ru.practicum.shareit.comment;

import java.util.Collection;
import java.util.List;

public interface CommentService {

    List<Comment> findByItemIds(Collection<Long> itemIds);

    Comment create(long itemId,long authorId, Comment comment);
}
