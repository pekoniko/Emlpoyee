package com.example.employee.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true, nullable = false)
    private Employee employee;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    public Salary(Employee employee, BigDecimal amount, LocalDate startDate) {
        this.employee = employee;
        this.amount = amount;
        this.startDate = startDate;
    }
}