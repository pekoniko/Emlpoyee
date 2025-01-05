package com.example.employee.repositories;

import com.example.employee.entities.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    public List<SalaryHistory> findByEmployeeId(Long id);

    public void deleteByEmployeeId(Long salaryInfo);

}