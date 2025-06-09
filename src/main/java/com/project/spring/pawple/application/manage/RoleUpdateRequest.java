package com.project.spring.pawple.application.manage;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleUpdateRequest {
    private List<String> roles = new ArrayList<>();

    

    public List<String> getRoles(){
        return roles;
    }

    public void setRoles(List<String> roles){
        this.roles = roles;
    }

    
    
}
