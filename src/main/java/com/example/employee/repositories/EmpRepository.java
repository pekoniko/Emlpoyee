package com.example.employee.repositories;

import com.example.employee.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<Employee, Long> {

    public List<Employee> findByFirstName(String firstName);

    public List<Employee> findByLastName(String lastName);

    public List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}