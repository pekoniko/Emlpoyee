package com.example.employee.dto;

import com.example.employee.entities.Salary;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;


public record JsonSalary(@NotBlank(message = "Employee id can't be empty") Long employeeId,
                         @NotBlank(message = "Amount can't be empty") @Min(0) BigDecimal amount,
                         @NotBlank(message = "Start date can't be empty") LocalDate startDate) {

    public JsonSalary(Salary salary) {
        this(salary.getEmployeeId(),
                salary.getAmount(),
                salary.getStartDate());
    }


}
