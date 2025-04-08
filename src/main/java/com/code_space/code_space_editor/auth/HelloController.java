package com.code_space.code_space_editor.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
