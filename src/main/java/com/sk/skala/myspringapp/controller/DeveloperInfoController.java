package com.sk.skala.myspringapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myspringapp.config.DeveloperProperties;
import com.sk.skala.myspringapp.dto.DeveloperInfo;

@RestController
@RequestMapping("/api")
public class DeveloperInfoController {

    private final DeveloperProperties props;

    public DeveloperInfoController(DeveloperProperties props) {
        this.props = props;
    }

    @GetMapping("/developer-info")
    public DeveloperInfo info() {
        var owner = props.getOwner();
        var team = props.getTeam();

        return new DeveloperInfo(owner, team);
    }
}
