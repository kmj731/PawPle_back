package com.project.spring.skillstack.dto;
import java.util.List;

public class UpdateUserRoleRequest {
    private Long userId;
    private List<String> roles;

    // 기본 생성자
    public UpdateUserRoleRequest() {}

    // 생성자 (선택사항)
    public UpdateUserRoleRequest(Long userId, List<String> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    // getter와 setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

