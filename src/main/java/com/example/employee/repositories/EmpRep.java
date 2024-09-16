package com.example.employee.repositories;

import com.example.employee.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpRep extends JpaRepository<Employee, Long> {

    public List<Employee> findByFirstName(String firstName);

    public List<Employee> findByLastName(String lastName);

    public List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}