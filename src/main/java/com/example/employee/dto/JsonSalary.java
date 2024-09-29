package com.example.employee.dto;

import com.example.employee.entities.Salary;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public @Data class JsonSalary extends Object{
    private String employeeId;
    private BigDecimal amount;
    private String startDate;
    public JsonSalary(Salary salary){
        this.employeeId = salary.getEmployeeId().toString();
        this.amount = salary.getAmount();
        this.startDate = salary.getStartDate().toString();
    }

}
