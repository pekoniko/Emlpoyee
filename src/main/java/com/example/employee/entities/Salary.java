package com.example.employee.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "salary")
@Getter
@Setter
@NoArgsConstructor
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    public Salary(Long employeeId, BigDecimal amount, LocalDate startDate) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.startDate = Date.valueOf(startDate);
    }

    public LocalDate getStartDate() {
        return startDate.toLocalDate();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = Date.valueOf(startDate);
    }
}