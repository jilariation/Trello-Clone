package ru.jilly.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.Comment;
import ru.jilly.trelloclone.dto.comment.CommentRequest;
import ru.jilly.trelloclone.dto.comment.CommentResponse;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    public CommentResponse toDto(Comment comment) {
        UserResponse userDto = userMapper.toDto(comment.getCreator());
        CommentResponse commentDto = modelMapper.map(comment, CommentResponse.class);
        commentDto.setCreator(userDto);
        return commentDto;
    }
    public Comment toEntity(CommentRequest commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }
    public void mergeDtoIntoEntity(CommentRequest commentDto, Comment comment) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(commentDto, comment);
    }
}
