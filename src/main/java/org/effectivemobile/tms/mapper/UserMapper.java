package org.effectivemobile.tms.mapper;

import org.effectivemobile.tms.dto.user.UserResponseDto;
import org.effectivemobile.tms.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto entityToResponseDto(User user);
}
