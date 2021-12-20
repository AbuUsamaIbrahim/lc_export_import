package com.pacific.restapi.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ForgotPasswordDto {
    @NotBlank(message = "Token can't be empty")
    private String token;
    @Size(min = 8, max = 100, message = "Password Must be within 8 to 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;
    @Size(min = 8, max = 100, message = "New Password Must be within 8 to 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String newPassword;
    @Size(min = 8, max = 100, message = "Confirm password Must be within 8 to 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character")
    private String confirmPassword;
}
