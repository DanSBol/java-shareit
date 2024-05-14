package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<CommentDto> mapToCommentsDto(List<Comment> comments) {
        return comments.stream()
            .map(CommentMapper::mapToCommentDto)
            .collect(Collectors.toList());
    }
}