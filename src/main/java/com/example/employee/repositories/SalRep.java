package com.example.employee.repositories;

import com.example.employee.entities.Employee;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalRep extends JpaRepository<Salary, Long> {

    public Salary findByEmployee_Id(long employeeId);
}