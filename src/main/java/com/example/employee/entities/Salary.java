package com.example.employee.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
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
    @Temporal(TemporalType.DATE)
    private Date startDate;

    public Salary(Long employeeId, BigDecimal amount, Date startDate) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate.toLocalDate();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = Date.valueOf(startDate);
    }
}