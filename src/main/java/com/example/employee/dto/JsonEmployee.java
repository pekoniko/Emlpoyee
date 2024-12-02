package com.example.employee.dto;

import com.example.employee.entities.Employee;
import com.example.employee.validation.ValidateName;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;


public record JsonEmployee(Long id,
                           @ValidateName
                           @NotBlank(message = "First name can't be empty", groups = OnCreate.class)
                           String firstName,
                           @ValidateName
                           @NotBlank(message = "Last name can't be empty", groups = OnCreate.class)
                           String lastName,
                           @Length(min = 3, max = 20, groups = OnCreate.class)
                           @NotBlank(message = "Position name can't be empty", groups = OnCreate.class)
                           String position,
                           @NotNull(groups = OnCreate.class)
                           LocalDate hireDate) {
    public interface OnCreate {}

    public JsonEmployee(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getPosition(), employee.getHireDate());
    }

}
