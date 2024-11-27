package com.example.employee.dto;

import com.example.employee.entities.Employee;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public record JsonEmployee(Long id,
                           @NotBlank(message = "First name can't be empty", groups = OnCreate.class) String firstName,
                           @NotBlank(message = "Last name can't be empty", groups = OnCreate.class) String lastName,
                           @NotBlank(message = "Position name can't be empty", groups = OnCreate.class) String position,
                           @NotNull(groups = OnCreate.class) LocalDate hireDate) {
    public interface OnCreate {}

    public JsonEmployee(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getPosition(), employee.getHireDate());
    }

}
