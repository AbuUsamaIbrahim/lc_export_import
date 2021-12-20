package com.pacific.restapi.service;

import com.pacific.restapi.dto.Response;
import com.pacific.restapi.dto.RoleDto;
import com.pacific.restapi.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    Response create(RoleDto roleDto);

    Response update(Long id, RoleDto roleDto);

    Response delete(Long id);

    Response get(Long id);

    Response getAll(Pageable pageable, boolean isExport, String search, String status);

    Response assignUsers(Long id, List<UserDto> userDtoList);
}
