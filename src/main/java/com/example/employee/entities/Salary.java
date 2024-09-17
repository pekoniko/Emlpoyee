package com.example.employee.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "salary")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true, nullable = false)
    private Employee employee;
//    private Long employeeId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

//    @OneToMany
//    @JoinColumn(name = "employee_id")
//    private List<SalaryHistory> salaryHistory;

    public Salary(){}

    public Salary(Employee employee, BigDecimal amount, Date startDate){
        this.employee = employee;
        this.amount = amount;
        this.startDate = startDate;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public Employee getEmployeeId() {
        return employee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmployeeId(Employee employeeId) {
        this.employee = employee;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}