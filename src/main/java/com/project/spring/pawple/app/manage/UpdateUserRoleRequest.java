package com.project.spring.pawple.app.manage;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleRequest {
    private Long userId;
    private List<String> roles;
}
