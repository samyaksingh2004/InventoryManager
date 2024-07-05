package com.example.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class MyController {
    @GetMapping("/")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
}
