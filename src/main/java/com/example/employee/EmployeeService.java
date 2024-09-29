package com.example.employee;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonReturn;
import com.example.employee.dto.JsonSalary;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import com.example.employee.repositories.EmpRepository;
import com.example.employee.repositories.SalHistRepository;
import com.example.employee.repositories.SalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private SalRepository salRepository;

    @Autowired
    private SalHistRepository salHistRepository;


    public JsonReturn<JsonEmployee> checkEmployeeLogic(JsonEmployee employee) {
        if (employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() ||
                employee.getPosition().isEmpty() || employee.getHireDate() == null)
            return new JsonReturn<>(null, "Date not in format yyyy-mm-dd", false);
        LocalDate date;
        try {
            date = LocalDate.parse(employee.getHireDate());
        } catch (DateTimeParseException e) {
            return new JsonReturn<>(null, "Data on employee not full", false);
        }
        Employee newEmployee = new Employee(employee.getFirstName(), employee.getLastName(), employee.getPosition(), date);
        empRepository.save(newEmployee);
        JsonEmployee result = new JsonEmployee(newEmployee);
        return new JsonReturn<>(getObjectMap(result, "employee"), "", true);
    }

    public JsonReturn<List<JsonEmployee>> getAllLogic() {
        List<Employee> all = empRepository.findAll();
        if (all.isEmpty())
            return new JsonReturn<>(null, "", true);
        Map<String, List<JsonEmployee>> result = getObjectMap(getJsonEmployeeList(all), "employees");
        return new JsonReturn<>(result, "", true);
    }

    public JsonReturn<JsonEmployee> getEmployeeByIdLogic(Long id) {
        Optional<Employee> employee = empRepository.findById(id);
        if (employee.isEmpty())
            return new JsonReturn<>(null, "No employee with that id", false);
        Map<String, JsonEmployee> result = getObjectMap(new JsonEmployee(employee.get()), "employee");
        return new JsonReturn<>(result, "", true);
    }

    public JsonReturn<List<JsonEmployee>> getEmployeeByNamesLogic(String firstName, String lastName) {
        List<Employee> employee = empRepository.findByFirstNameAndLastName(firstName, lastName);
        if (employee.isEmpty())
            return new JsonReturn<>(null, "No employee with that name and last name", false);
        Map<String, List<JsonEmployee>> result = getObjectMap(getJsonEmployeeList(employee), "employees");
        return new JsonReturn<>(result, "", true);
    }

    public JsonReturn<String> getSalaryOnDateLogic(Long id, String dateString) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn<>(null, "No employee with that ID", false);
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        if (LocalDate.now().isBefore(date))
            return new JsonReturn<>(null, "Can't search in future", false);

        List<SalaryHistory> allHistory = salHistRepository.findByEmployeeId(id);
        allHistory.sort((o1, o2) -> {
            if (o1.getStartDate().isAfter(o2.getStartDate())) return 1;
            if (o2.getStartDate().isAfter(o1.getStartDate())) return -1;
            return 0;
        });
        if (date.isBefore(allHistory.get(0).getStartDate())) // in case selected date before hiring
            return new JsonReturn<>(null, "Date is before first record", false);
        for (SalaryHistory oneObj : allHistory) {
            if (date.isAfter(oneObj.getStartDate()) || date.equals(oneObj.getStartDate()) &&
                    (oneObj.getEndDate() == null ||
                            date.isBefore(oneObj.getEndDate()) || date.equals(oneObj.getEndDate()))) {
                Map<String, String> result = getObjectMap(oneObj.getAmount().toString(), "amount");
                return new JsonReturn<>(result, "", true);
            }
        }

        return new JsonReturn<>(null, "Salary not found on selected date", false);
    }


    public JsonReturn<JsonSalary> getSalaryLogic(Long id) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn<>(null, "No employee with that ID", false);
        Salary salary = salRepository.findByEmployeeId(id);
        if (salary == null) // in case selected date before hiring
            return new JsonReturn<>(null, "Salary still wasn't set for that employee", false);
        Map<String, JsonSalary> result = getObjectMap(new JsonSalary(salary), "salary");
        return new JsonReturn<>(result, "", true);
    }

    public JsonReturn<JsonEmployee> updateEmployeeLogic(Long id, JsonEmployee employeeInfo) {
        Optional<Employee> existingEmployee = empRepository.findById(id);
        if (existingEmployee.isEmpty())
            return new JsonReturn<>(null, "No employee by that ID", false);
        if (employeeInfo.getFirstName().isEmpty() || employeeInfo.getLastName().isEmpty() ||
                employeeInfo.getPosition().isEmpty() || employeeInfo.getHireDate() == null)
            return new JsonReturn<>(null, "New data on employee not full", false);
        LocalDate date;
        try {
            date = LocalDate.parse(employeeInfo.getHireDate());
        } catch (DateTimeParseException e) {
            return new JsonReturn<>(null, "Data on employee not full", false);
        }
        existingEmployee.get().setFirstName(employeeInfo.getFirstName());
        existingEmployee.get().setLastName(employeeInfo.getLastName());
        existingEmployee.get().setPosition(employeeInfo.getPosition());
        existingEmployee.get().setHireDate(Date.valueOf(date));
        empRepository.save(existingEmployee.get());
        Map<String, JsonEmployee> result = getObjectMap(new JsonEmployee(existingEmployee.get()), "employee");
        return new JsonReturn<>(result, "", true);
    }

    public JsonReturn<String> updateAmountLogic(Long id, JsonSalary newSalaryInfo) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn<>(null, "Have no employee with this id", false);
        Salary existingSalary = salRepository.findByEmployeeId(id);
        if (newSalaryInfo.getAmount() == null)
            return new JsonReturn<>(null, "No amount in salary specified ", false);
        if (existingSalary != null && newSalaryInfo.getAmount().equals(existingSalary.getAmount()))
            return new JsonReturn<>(null, newSalaryInfo.getAmount() + " is already set as amount!", false);

        if (existingSalary == null) //if salary already set up replace old
            existingSalary = new Salary();
        LocalDate date = null;
        try {
            date = LocalDate.parse(newSalaryInfo.getStartDate());
            existingSalary.setStartDate(date.plusDays(1));
        } catch (Exception ignored) {
        }
        if (date == null)
            existingSalary.setStartDate(LocalDate.now());
        existingSalary.setAmount(newSalaryInfo.getAmount());
        existingSalary.setEmployeeId(id);
        salRepository.save(existingSalary);

        List<SalaryHistory> salaryHistoryList = salHistRepository.findByEmployeeId(id);
        salaryHistoryList.sort((o1, o2) -> {
            if (o1.getStartDate().isAfter(o2.getStartDate())) return 1;
            if (o2.getStartDate().isAfter(o1.getStartDate())) return -1;
            return 0;
        });
        if (!salaryHistoryList.isEmpty() && salaryHistoryList.get(salaryHistoryList.size() - 1).getEndDate() == null)
            salaryHistoryList.get(salaryHistoryList.size() - 1).setEndDate(LocalDate.now());
        salHistRepository.save(new SalaryHistory(id, newSalaryInfo.getAmount(),
                Date.valueOf(existingSalary.getStartDate()), null));
        Map<String, String> result = getObjectMap(newSalaryInfo.getAmount().toString(), "amount");
        return new JsonReturn<>(result, "", true);
    }


    public JsonReturn<String> deleteEmployeeLogic(Long id) {
        try {
            empRepository.deleteById(id);
            return new JsonReturn<>(null, "", true);
        } catch (Exception e) {
            log.error("Error with deleting by ID - " + id + ". " + e);
            return new JsonReturn<>(null, "", false);
        }
    }

    public List<JsonEmployee> getJsonEmployeeList(List<Employee> employees) {
        List<JsonEmployee> jsonExit = new ArrayList<>();
        for (Employee employee : employees)
            jsonExit.add(new JsonEmployee(employee));
        return jsonExit;
    }

    private <T> Map<String, T> getObjectMap(T obj, String listName) {
        Map<String, T> result = new HashMap<>();
        result.put(listName, obj);
        return result;
    }
}
