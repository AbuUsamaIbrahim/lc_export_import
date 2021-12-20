package com.pacific.restapi.service;

import com.pacific.restapi.dto.LoginRequestDto;
import com.pacific.restapi.dto.Response;

public interface AuthService {
    Response login(LoginRequestDto loginRequestDto);

    Response logout(LoginRequestDto loginRequestDto);
}
