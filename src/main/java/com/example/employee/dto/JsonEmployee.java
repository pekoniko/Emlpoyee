package com.example.employee.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public @Data class JsonEmployee {
    String firstName;
    String lastName;
    String position;
    String hireDate;
}
