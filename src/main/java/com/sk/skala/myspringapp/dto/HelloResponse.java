package com.sk.skala.myspringapp.dto;

import lombok.Data;

@Data
public class HelloResponse {
    private int statusCode;
    private String message;
}
