package com.project.spring.skillstack.controller.permit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.spring.skillstack.dao.UserRepository;



@RestController
@RequestMapping("/permit")
public class Permit {

    @Autowired
    UserRepository userRep;

    @GetMapping("/test")
    public List<String> getMethodName() {
        return List.of("Hello", "Bye");
    }
    
    
}
