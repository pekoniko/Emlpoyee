package com.example.employee.repositories;

import com.example.employee.entities.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalRepository extends JpaRepository<Salary, Long> {

    public Salary findByEmployee_Id(long employeeId);
}