package org.effectivemobile.tms.mapper;


import org.effectivemobile.tms.dto.comment.CommentResponseDto;
import org.effectivemobile.tms.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "task.id", target = "taskId")
    CommentResponseDto entityToResponseDto(Comment comment);
}
