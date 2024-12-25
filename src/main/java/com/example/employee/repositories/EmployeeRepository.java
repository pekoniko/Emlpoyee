package com.example.employee.repositories;

import com.example.employee.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    @Query("""
            select a from Employee a where
            (a.firstName = ?1)
            and (a.lastName= ?2)
            and (a.position = ?3)
            and (a.hireDate = ?4)
            """)
    List<Employee> findEmployee(String firstName, String lastName, String position, LocalDate hireDate); //todo search by method name!!

    @Query("""
            select a from Employee a where
            (?1 is null or a.firstName = ?1)
            and (?2 is null or a.lastName= ?2)
            and (?3 is null or a.position = ?3)
            """)
    List<Employee> findFiltered(String firstName, String lastName, String position);
}