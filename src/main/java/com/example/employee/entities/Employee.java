package com.example.employee.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private Date hireDate;


    public Employee(String firstName, String lastName, String position, LocalDate hireDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.hireDate = Date.valueOf(hireDate);
    }

    public Employee(Employee employee) {
        this.id = employee.id;
        this.firstName = employee.firstName;
        this.lastName = employee.lastName;
        this.position = employee.position;
        this.hireDate = employee.hireDate;
    }

    public LocalDate getHireDate() {
        return hireDate.toLocalDate();
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = Date.valueOf(hireDate);
    }
}