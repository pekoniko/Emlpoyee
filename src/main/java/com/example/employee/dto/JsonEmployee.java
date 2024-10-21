package com.example.employee.dto;

import com.example.employee.entities.Employee;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.time.LocalDate;

public record JsonEmployee(BigInteger id,
                           @NotNull String firstName,
                           @NotNull String lastName,
                           @NotNull String position,
                           @NotNull LocalDate hireDate) {

    public JsonEmployee(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getPosition(), employee.getHireDate());
    }

}
