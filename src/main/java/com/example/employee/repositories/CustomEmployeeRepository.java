package com.example.employee.repositories;

import com.example.employee.entities.Employee;

import java.time.LocalDate;
import java.util.List;

public interface CustomEmployeeRepository {
    List<Employee> findFiltered(String firstName, String lastName, String position, LocalDate hireDate);
}
