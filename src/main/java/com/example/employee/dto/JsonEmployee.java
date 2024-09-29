package com.example.employee.dto;

import com.example.employee.entities.Employee;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JsonEmployee extends Object{
    Long id;
    String firstName;
    String lastName;
    String position;
    String hireDate;

    public JsonEmployee(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.hireDate = employee.getHireDate().toString();
        this.position = employee.getPosition();
    }
}
