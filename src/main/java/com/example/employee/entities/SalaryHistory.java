package com.example.employee.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salary_history")
@Getter
@Setter
@NoArgsConstructor
public class SalaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    public SalaryHistory(Long employeeId, BigDecimal amount, LocalDate startDate, LocalDate endDate) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}