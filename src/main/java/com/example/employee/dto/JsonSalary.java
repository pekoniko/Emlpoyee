package com.example.employee.dto;

import com.example.employee.entities.Salary;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;


public record JsonSalary(@NotNull Long employeeId,
                         @NotNull BigDecimal amount,
                         @NotNull LocalDate startDate) {

    public JsonSalary(Salary salary) {
        this(salary.getEmployeeId(),
                salary.getAmount(),
                salary.getStartDate());
    }


}
