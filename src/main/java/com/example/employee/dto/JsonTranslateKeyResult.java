package com.example.employee.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JsonTranslateKeyResult {
    private String iamToken;
    private String expiresAt;
}
