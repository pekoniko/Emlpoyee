package com.example.employee.service;

import com.example.employee.dto.*;
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

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;
    private final YandexTranslateService translateService;

    @Transactional
    public JsonReturn<JsonEmployee> createEmployee(JsonEmployee employee) {
        LocalDate date = employee.hireDate();
        if (!employeeRepository.findFiltered(employee.firstName(), employee.lastName(), employee.position(), date).isEmpty())
            return makeUnsuccessfulReturn("That employee already exist");
        Employee newEmployee = new Employee(employee.firstName(), employee.lastName(), employee.position(), date);
        employeeRepository.save(newEmployee);
        return makeSuccessfulReturn("employee", new JsonEmployee(newEmployee));
    }

    @Transactional
    public JsonReturn<List<JsonEmployee>> getAll(LinkedHashMap<String, String> params) {
        List<Employee> all =
                employeeRepository.findFiltered(params.get("firstName"), params.get("lastName"), params.get("position"), null);
        if (all.isEmpty()) {
            return makeUnsuccessfulReturn("No employees with that parameters");
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
    public JsonReturn<JsonEmployee> getTranslatedPosition(Long id, String language) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that Id");
        }
        String translatedPosition;
        try {
            translatedPosition = translateService.getTranslation(language, employee.get().getPosition());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(translatedPosition)) {
            Employee clonedEmployee = new Employee(employee.get());
            clonedEmployee.setPosition(translatedPosition);
            JsonEmployee result = new JsonEmployee(clonedEmployee);
            return makeSuccessfulReturn("employee", result);
        }
        return makeUnsuccessfulReturn("Translation was unsuccessful.");

    }

    @Transactional
    public JsonReturn<String> getSalaryOnDate(Long id, String dateString) {
        if (employeeRepository.findById(id).isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that id");
        }
        if (dateString == null) {
            return getSalary(id);
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

    public JsonReturn<String> getSalary(Long id) {
        Salary salary = salaryRepository.findByEmployeeId(id);
        if (salary == null) { // in case selected date before hiring
            return makeUnsuccessfulReturn("Salary still wasn't set for that employee");
        }
        return makeSuccessfulReturn("salary", salary.getAmount().toString());
    }

    @Transactional
    public JsonReturn<JsonEmployee> updateEmployee(Long id, JsonEmployee employeeInfo) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            return makeUnsuccessfulReturn("No employee by that id");
        }

        Employee existingEmployee = optionalEmployee.get();
        if (employeeInfo.firstName() != null && !employeeInfo.firstName().isEmpty()) {
            existingEmployee.setFirstName(employeeInfo.firstName());
        }
        if (employeeInfo.lastName() != null && !employeeInfo.lastName().isEmpty()) {
            existingEmployee.setLastName(employeeInfo.lastName());
        }
        if (employeeInfo.position() != null && !employeeInfo.position().isEmpty()) {
            existingEmployee.setPosition(employeeInfo.position());
        }
        if (employeeInfo.hireDate() != null) {
            existingEmployee.setHireDate(employeeInfo.hireDate());
        }
        employeeRepository.save(existingEmployee);
        return makeSuccessfulReturn("employee", new JsonEmployee(existingEmployee));
    }

    @Transactional
    public JsonReturn<String> updateAmount(Long id, JsonSalary newSalaryInfo) {
        if (employeeRepository.findById(id).isEmpty()) {
            return makeUnsuccessfulReturn("Have no employee with this id");
        }
        Salary existingSalary = salaryRepository.findByEmployeeId(id);
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
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return makeUnsuccessfulReturn("No employee with that Id");
        }
        employeeRepository.deleteById(id);
        return makeSuccessfulReturn(null, null);
    }

    public <T> JsonReturn<T> makeSuccessfulReturn(String listName, T data) {
        if (listName == null) {
            return new JsonReturn<>(null, "", true);
        }
        return new JsonReturn<>(Map.of(listName, data), "", true);
    }

    public static <T> JsonReturn<T> makeUnsuccessfulReturn(String errorMessage) {
        return new JsonReturn<>(null, errorMessage, true);
    }

    public List<JsonEmployee> getJsonEmployeeList(List<Employee> employees) {
        return employees.stream().map(JsonEmployee::new).toList();
    }

}
