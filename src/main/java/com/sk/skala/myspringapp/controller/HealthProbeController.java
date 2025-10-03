package com.sk.skala.myspringapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthProbeController {

    @GetMapping({"/probe/ready", "/probe/alive"})
    public ResponseEntity<String> probe() {
      return ResponseEntity.ok("OK");
    }
}
