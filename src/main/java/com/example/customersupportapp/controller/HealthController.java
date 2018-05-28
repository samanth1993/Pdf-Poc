package com.example.customersupportapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthController {

    private static final String HEALTH_OK="Customer Support App is UP !";

    @RequestMapping("/health")
    public String status(){
        return HEALTH_OK;
    }

}
