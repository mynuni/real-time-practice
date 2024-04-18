package com.practice.springrealtime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/polling")
    public String getPollingPage() {
        return "/polling/polling";
    }

}
