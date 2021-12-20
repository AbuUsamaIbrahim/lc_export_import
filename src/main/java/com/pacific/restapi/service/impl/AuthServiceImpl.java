package com.pacific.restapi.service.impl;

import com.pacific.restapi.dto.*;
import com.pacific.restapi.jwtconfig.JwtTokenProvider;
import com.pacific.restapi.service.AuthService;
import com.pacific.restapi.service.UserService;
import com.pacific.restapi.model.User;
import com.pacific.restapi.util.ResponseBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service("authService")
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public Response login(LoginRequestDto loginRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmailOrUserName(), loginRequestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (authentication.isAuthenticated()) {
                LoginResponseDto loginResponseDto = buildUserResponseDto(authentication);
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, loginResponseDto, "Logged In Successfully.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Password or username is incorrect. Try again.");
        } catch (Exception e) {
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Password or username is incorrect. Try again.");
        }
    }

    private LoginResponseDto buildUserResponseDto(Authentication authentication) {
        String token = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<RoleDto> roleDtos = new ArrayList<>();
        userPrincipal.getAuthorities().forEach(grantedAuthority -> {
            RoleDto roleDto = new RoleDto();
            roleDto.setName(grantedAuthority.getAuthority());
            roleDtos.add(roleDto);
        });
        return new LoginResponseDto(token, userPrincipal.getEmail(), userPrincipal.getName(), userPrincipal.getId(), roleDtos);
    }


    @Override
    public Response logout(LoginRequestDto loginRequestDto) {

        return null;
    }
}
