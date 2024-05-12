package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment mapToComment(CommentDto commentDto, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto.CommentDtoBuilder()
            .id(comment.getId())
            .authorName(comment.getAuthor().getName())
            .itemId(comment.getItem().getId())
            .text(comment.getText())
            .created(comment.getCreated().toString())
            .build();
    }

    public static List<CommentDto> mapToCommentsDto(Iterable<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(mapToCommentDto(comment));
        }
        return commentsDto;
    }
}