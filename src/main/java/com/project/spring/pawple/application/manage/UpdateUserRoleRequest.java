package com.project.spring.pawple.application.manage;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleRequest {
    private Long userId;
    private List<String> roles;
}
