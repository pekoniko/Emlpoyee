package com.example.employee.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class JsonReturn<T> {
    Map<String, T> result;
    String error;
    boolean success;

    public JsonReturn(Map<String, T> result, String error, boolean success) {
        this.result = result;
        this.error = error;
        this.success = success;
    }

}
