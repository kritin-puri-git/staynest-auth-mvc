package com.project.staynest.auth.presentation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${mysql.staynest-mysql-db-master-username}")
    private String username;

    @GetMapping("/test")
    public String test(){
        return username;
    }
}
