package com.pacific.restapi.aspect;

import com.pacific.restapi.dto.Response;
import com.pacific.restapi.util.ResponseBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Aspect
@Configuration
public class DataValidationAspect {
    @Around("@annotation(com.pacific.restapi.annotations.DataValidation) && args(..))")
    public Response createValidate(ProceedingJoinPoint joinPoint) {
        Object[] signatureArgs = joinPoint.getArgs();
        BindingResult bindingResult = null;
        for (int i = 0; i < signatureArgs.length; i++) {
            if (signatureArgs[i] instanceof BindingResult) {
                bindingResult = (BindingResult) signatureArgs[i];
            }
        }
        if (bindingResult != null && bindingResult.hasErrors()) {
            return ResponseBuilder.getFailResponse(bindingResult, "Backend validation failed");
        }
        try {
            Response response = (Response) joinPoint.proceed();
            return response;
        } catch (Throwable throwable) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server error");
        }
    }
}
