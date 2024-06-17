package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

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
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated().toString());
        return commentDto;
    }

    public static List<CommentDto> mapToCommentsDto(List<Comment> comments) {
        return comments.stream()
            .map(CommentMapper::mapToCommentDto)
            .collect(Collectors.toList());
    }
}