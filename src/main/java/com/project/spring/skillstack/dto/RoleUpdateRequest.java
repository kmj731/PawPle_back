package com.project.spring.skillstack.dto;

import java.util.List;

public class RoleUpdateRequest {
    private List<String> roles;

    public RoleUpdateRequest(){}

    public List<String> getRoles(){
        return roles;
    }

    public void setRoles(List<String> roles){
        this.roles = roles;
    }
    
}
