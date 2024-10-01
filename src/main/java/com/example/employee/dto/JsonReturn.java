package com.example.employee.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Builder
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
