package com.example.employee.dto;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JsonEmployee {
    String firstName;
    String lastName;
    String position;
    String hireDate;


    public JsonEmployee(String firstName, String lastName, String position, String hireDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.hireDate = hireDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public String getHireDate() {
        return hireDate;
    }

}
