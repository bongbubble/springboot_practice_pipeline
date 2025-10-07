package com.sk.skala.myspringapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the bundled SPA entry point when the root URL is requested.
 * This prevents the default Whitelabel error page from appearing when
 * users visit https://sk085-ingress.skala25a.project.skala-ai.com/ directly.
 */
@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
