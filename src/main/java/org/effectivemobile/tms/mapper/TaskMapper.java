package org.effectivemobile.tms.mapper;

import org.effectivemobile.tms.dto.task.TaskResponseDto;
import org.effectivemobile.tms.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "executor.id", target = "executorId")
    TaskResponseDto entityToResponseDto(Task task);
}
