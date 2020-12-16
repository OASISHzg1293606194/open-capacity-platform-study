package com.open.capacity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginError {

    @RequestMapping("/dashboard/login")
    public String dashboard() {
        return "redirect:/#/";
    }

    @RequestMapping("/login")
    public String login() {
        return "redirect:/#/";
    }
}

