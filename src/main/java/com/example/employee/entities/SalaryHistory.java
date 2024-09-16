package com.example.employee.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "salary_history")
public class SalaryHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    public SalaryHistory() {
    }

    public SalaryHistory(Employee employee, double amount, Date startDate, Date endDate) {
        this.employee = employee;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public double getAmount() {
        return amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}