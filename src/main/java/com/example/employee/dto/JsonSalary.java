package com.example.employee.dto;

import lombok.Data;
import java.math.BigDecimal;



public @Data class JsonSalary {
    private String employeeId;
    private BigDecimal amount;
    private String startDate;

}
