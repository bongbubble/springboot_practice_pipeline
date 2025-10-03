package com.sk.skala.myspringapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sk.skala.myspringapp.dto.Owner;
import com.sk.skala.myspringapp.dto.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ConfigurationProperties(prefix = "developer")
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperProperties {

    private Owner owner;
    private Team team;
}
