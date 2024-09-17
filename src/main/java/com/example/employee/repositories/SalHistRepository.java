package com.example.employee.repositories;

import com.example.employee.entities.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalHistRepository extends JpaRepository<SalaryHistory, Long> {

    public List<SalaryHistory> findByEmployee_Id(Long id);

    public void deleteByEmployee_Id(Long salaryInfo);
}