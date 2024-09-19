package com.example.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class JsonSalary {
    private String employeeId;
    private BigDecimal amount;
    private String startDate;

    public JsonSalary(){

    }

    public JsonSalary(String employeeId, BigDecimal amount, String startDate) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.startDate = startDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStartDate() {
        return startDate;
    }
}
