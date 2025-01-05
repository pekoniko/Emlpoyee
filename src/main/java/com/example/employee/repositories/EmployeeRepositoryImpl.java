package com.example.employee.repositories;

import com.example.employee.entities.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class EmployeeRepositoryImpl implements CustomEmployeeRepository {

    private EntityManager entityManager;

    @Override
    public List<Employee> findFiltered(String firstName, String lastName, String position, LocalDate hireDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> employee = cq.from(Employee.class);
        List<Predicate> predicates = new ArrayList<>();
        if (firstName != null) {
            predicates.add(cb.equal(employee.get("firstName"), firstName));
        }
        if (lastName != null) {
            predicates.add(cb.like(employee.get("lastName"), lastName));
        }
        if (position != null) {
            predicates.add(cb.like(employee.get("position"), position));
        }
        if (hireDate != null) {
            predicates.add(cb.equal(employee.get("hireDate"), hireDate));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }
}
