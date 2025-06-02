package com.project.spring.skillstack.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleRequest {
    private Long userId;
    private List<String> roles;
}
