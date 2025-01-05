package com.example.employee.dto;

import java.util.Map;

public record JsonReturn<T>(Map<String, T> result, String error, boolean success) {
}
