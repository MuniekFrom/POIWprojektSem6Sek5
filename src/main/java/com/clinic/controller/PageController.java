package com.clinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/favicon.ico")
    public String favicon() {
        return "forward:/images/favicon.png";
    }

    @GetMapping("/")
    public String redirectToHomePage() {
        return "redirect:/ZapisyDoLekarza";
    }

    @GetMapping("/ZapisyDoLekarza")
    public String homePage() {
        return "forward:/index.html";
    }
}