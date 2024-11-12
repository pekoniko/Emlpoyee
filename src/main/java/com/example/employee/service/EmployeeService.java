package com.example.employee.service;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonReturn;
import com.example.employee.dto.JsonSalary;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import com.example.employee.repositories.EmployeeRepository;
import com.example.employee.repositories.SalaryHistoryRepository;
import com.example.employee.repositories.SalaryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;

    @Transactional
    public JsonReturn<JsonEmployee> createEmployee(JsonEmployee employee) {
        LocalDate date = employee.hireDate();
        Employee newEmployee = new Employee(employee.firstName(), employee.lastName(), employee.position(), date);
        employeeRepository.save(newEmployee);
        return makeSuccessfulReturn("employee", new JsonEmployee(newEmployee));
    }

    @Transactional
    public JsonReturn<List<JsonEmployee>> getAll() {
        List<Employee> all = employeeRepository.findAll();
        if (all.isEmpty()) {
            return makeUnsuccessfulReturn("No employees");
        }
        return makeSuccessfulReturn("employees", getJsonEmployeeList(all));
    }

    @Transactional
    public JsonReturn<JsonEmployee> getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that Id");
        }
        return makeSuccessfulReturn("employee", new JsonEmployee(employee.get()));
    }

    @Transactional
    public JsonReturn<List<JsonEmployee>> getEmployeeByNames(String firstName, String lastName) {
        List<Employee> employee = employeeRepository.findByFirstNameAndLastName(firstName, lastName);
        if (employee.isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that name and last name");
        }
        return makeSuccessfulReturn("employees", getJsonEmployeeList(employee));
    }

    @Transactional
    public JsonReturn<String> getSalaryOnDate(Long id, String dateString) {
        if (employeeRepository.findById(id).isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that id");
        }
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        if (LocalDate.now().isBefore(date)) {
            return makeUnsuccessfulReturn("Can't search in future");
        }
        List<SalaryHistory> allHistory = salaryHistoryRepository.findByEmployeeId(id).stream().
                sorted(Comparator.comparing(SalaryHistory::getStartDate)).toList();
        if (date.isBefore(allHistory.get(0).getStartDate())) { // in case selected date before hiring
            return makeUnsuccessfulReturn("Date is before first record");
        }
        for (SalaryHistory oneObj : allHistory) {
            if (date.isAfter(oneObj.getStartDate()) || date.equals(oneObj.getStartDate()) &&
                    (oneObj.getEndDate() == null ||
                            date.isBefore(oneObj.getEndDate()) || date.equals(oneObj.getEndDate()))) {
                return makeSuccessfulReturn("amount", oneObj.getAmount().toString());
            }
        }
        return makeUnsuccessfulReturn("Salary not found on selected date");
    }

    @Transactional
    public JsonReturn<JsonSalary> getSalary(Long id) {
        if (employeeRepository.findById(id).isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that id");
        }
        Salary salary = salaryRepository.findByEmployeeId(id);
        if (salary == null) { // in case selected date before hiring
            return makeUnsuccessfulReturn("Salary still wasn't set for that employee");
        }
        return makeSuccessfulReturn("salary", new JsonSalary(salary));
    }

    @Transactional
    public JsonReturn<JsonEmployee> updateEmployee(Long id, JsonEmployee employeeInfo) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            return makeUnsuccessfulReturn("No employee by that id");
        }
        LocalDate date = employeeInfo.hireDate();
        Employee existingEmployee = optionalEmployee.get();
        existingEmployee.setFirstName(employeeInfo.firstName());
        existingEmployee.setLastName(employeeInfo.lastName());
        existingEmployee.setPosition(employeeInfo.position());
        existingEmployee.setHireDate(date);
        employeeRepository.save(existingEmployee);
        return makeSuccessfulReturn("employee", new JsonEmployee(existingEmployee));
    }

    @Transactional
    public JsonReturn<String> updateAmount(Long id, JsonSalary newSalaryInfo) {
        if (employeeRepository.findById(id).isEmpty()) {
            return makeUnsuccessfulReturn("Have no employee with this id");
        }
        Salary existingSalary = salaryRepository.findByEmployeeId(id);
        if (newSalaryInfo.amount() == null) {
            return makeUnsuccessfulReturn("No amount in salary specified");
        }
        if (existingSalary != null && newSalaryInfo.amount().compareTo(existingSalary.getAmount()) == 0) {
            return makeUnsuccessfulReturn(newSalaryInfo.amount() + " is already set as amount!");
        }
        if (existingSalary == null) { //if salary already set up replace old
            existingSalary = new Salary();
        }
        LocalDate date = newSalaryInfo.startDate();
        if (date == null) {
            date = LocalDate.now();
        }
        existingSalary.setStartDate(date);
        existingSalary.setAmount(newSalaryInfo.amount());
        existingSalary.setEmployeeId(id);
        salaryRepository.save(existingSalary);
        List<SalaryHistory> salaryHistoryList = salaryHistoryRepository.findByEmployeeId(id).stream().
                sorted(Comparator.comparing(SalaryHistory::getStartDate)).toList();
        if (!salaryHistoryList.isEmpty() && salaryHistoryList.get(salaryHistoryList.size() - 1).getEndDate() == null) {
            salaryHistoryList.get(salaryHistoryList.size() - 1).setEndDate(date.minusDays(1));
        }
        salaryHistoryRepository.save(new SalaryHistory(id, newSalaryInfo.amount(),
                Date.valueOf(existingSalary.getStartDate()), null));
        return makeSuccessfulReturn("amount", newSalaryInfo.amount().toString());
    }

    @Transactional
    public JsonReturn<String> deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
        return makeSuccessfulReturn(null, null);
    }

    private <T> JsonReturn<T> makeSuccessfulReturn(String listName, T data) {
        if (listName == null) {
            return new JsonReturn<>(null, "", true);
        }
        return new JsonReturn<>(Map.of(listName, data), "", true);
    }

    private <T> JsonReturn<T> makeUnsuccessfulReturn(String errorMessage) {
        return new JsonReturn<>(null, errorMessage, true);
    }

    public List<JsonEmployee> getJsonEmployeeList(List<Employee> employees) {
        return employees.stream().map(JsonEmployee::new).toList();
    }

}
