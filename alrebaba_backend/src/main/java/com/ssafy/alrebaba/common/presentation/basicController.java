package com.ssafy.alrebaba.common.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class basicController {

    @GetMapping
    public String basicResponse(){
        return "backend is running!";
    }
}
