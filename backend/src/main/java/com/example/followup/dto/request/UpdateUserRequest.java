package com.example.followup.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdateUserRequest {
    private String password;
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "ADMIN|DOCTOR", message = "角色不合法")
    private String role;
    private String phone;
}
