package com.example.employee.dto;

import com.example.employee.entities.Employee;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;


public record JsonEmployee(Long id,
                           @NotBlank(message = "First name can't be empty", groups = OnCreate.class)
                           @Pattern(regexp = "^[a-zA-Z]{1,20}$", message = "First name should consist from english " +
                                   "letters and be at least one character long.",
                                   groups = {OnCreate.class, OnUpdate.class})
                           String firstName,
                           @NotBlank(message = "Last name can't be empty", groups = OnCreate.class)
                           @Pattern(regexp = "^[a-zA-Z]{1,20}$", message = "Last name should consist from english " +
                                   "letters and be at least one character long.",
                                   groups = {OnCreate.class, OnUpdate.class})
                           String lastName,
                           @Length(min = 3, max = 20, groups = OnCreate.class)
                           @NotBlank(message = "Position name can't be empty", groups = OnCreate.class)
                           String position,
                           @NotNull(groups = OnCreate.class)
                           LocalDate hireDate) {
    public interface OnCreate {
    }
    public interface OnUpdate {
    }

    public JsonEmployee(Employee employee) {
        this(employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getPosition(), employee.getHireDate());
    }

}
