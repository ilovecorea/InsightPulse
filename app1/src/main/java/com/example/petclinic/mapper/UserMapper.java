package com.example.petclinic.mapper;

import com.example.petclinic.model.Role;
import com.example.petclinic.model.User;
import com.example.petclinic.rest.dto.RoleDto;
import com.example.petclinic.rest.dto.UserDto;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * Map User/Role & UserDto/RoleDto using mapstruct
 */
@Mapper
public interface UserMapper {
    Role toRole(RoleDto roleDto);

    RoleDto toRoleDto(Role role);

    List<RoleDto> toRoleDtos(List<Role> roles);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    List<Role> toRoles(List<RoleDto> roleDtos);

}
