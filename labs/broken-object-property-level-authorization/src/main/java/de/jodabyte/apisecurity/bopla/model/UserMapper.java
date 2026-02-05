package de.jodabyte.apisecurity.bopla.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    User map(UserCreateDto userCreateDto);

    UserDto map(User user);

    UserPublicDto mapToPublic(User user);
}
