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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private SalRepository salRepository;

    @Autowired
    private SalHistRepository salHistRepository;


    public JsonReturn checkEmployeeLogic(JsonEmployee employee) {
        if (employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() ||
                employee.getPosition().isEmpty() || employee.getHireDate() == null)
            return new JsonReturn(null, "Date not in format yyyy-mm-dd", false);
        LocalDate date;
        try {
            date = LocalDate.parse(employee.getHireDate());
        } catch (DateTimeParseException e) {
            return new JsonReturn(employee, "Data on employee not full", false);

        }
        Employee newEmployee = new Employee(employee.getFirstName(), employee.getLastName(), employee.getPosition(), date);
        empRepository.save(newEmployee);
        return new JsonReturn(employee, "", true);
    }

    public JsonReturn getAllLogic() {
        List<Employee> all = empRepository.findAll();
        if (all.isEmpty())
            return new JsonReturn(all, "Employee list is empty", true);
        return new JsonReturn(all, "", true);
    }

    public JsonReturn getEmployeeByIdLogic(Long id) {
        Optional<Employee> employee = empRepository.findById(id);
        if (employee.isEmpty())
            return new JsonReturn(employee, "No employee with that id", false);
        return new JsonReturn(Arrays.asList(employee.get()), "", true);
    }

    public JsonReturn getEmployeeByNamesLogic(String firstName, String lastName) {
        List<Employee> employee = empRepository.findByFirstNameAndLastName(firstName, lastName);
        if (employee.isEmpty())
            return new JsonReturn(employee, "No employee with that name and last name", false);
        return new JsonReturn(employee, "", true);
    }

    public JsonReturn getSalaryOnDateLogic(Long id, String dateString) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn(null, "No employee with that ID", false);
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        if (LocalDate.now().isBefore(date))
            return new JsonReturn(null, "Can't search in future", false);

        List<SalaryHistory> allHistory = salHistRepository.findByEmployee_Id(id);
        allHistory.sort((o1, o2) -> {
            if (o1.getStartDate().isAfter(o2.getStartDate())) return 1;
            if (o2.getStartDate().isAfter(o1.getStartDate())) return -1;
            return 0;
        });
        if (date.isBefore(allHistory.get(0).getStartDate())) // in case selected date before hiring
            return new JsonReturn(null, "Date is before first record", false);
        for (SalaryHistory oneObj : allHistory)
            if (date.isAfter(oneObj.getStartDate()) || date.equals(oneObj.getStartDate()) &&
                    (oneObj.getEndDate() == null ||
                            date.isBefore(oneObj.getEndDate()) || date.equals(oneObj.getEndDate())))
                return new JsonReturn(oneObj.getAmount(), "", true);

        return new JsonReturn(null, "Salary not found on selected date", false);
    }


    public JsonReturn getSalaryLogic(Long id) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn(null, "No employee with that ID", false);
        Salary salary = salRepository.findByEmployee_Id(id);
        if (salary == null) // in case selected date before hiring
            return new JsonReturn(null, "Salary still wasn't set for that employee", false);
        return new JsonReturn(salary.getAmount().toString(), "", true);
    }

    public JsonReturn updateEmployeeLogic(Long id, JsonEmployee employeeInfo) {
        Employee existingEmployee = empRepository.findById(id).get();
        if (employeeInfo.getFirstName().isEmpty() || employeeInfo.getLastName().isEmpty() ||
                employeeInfo.getPosition().isEmpty() || employeeInfo.getHireDate() == null)
            return new JsonReturn(null, "New data on employee not full", false);
        LocalDate date;
        try {
            date = LocalDate.parse(employeeInfo.getHireDate());
        } catch (DateTimeParseException e) {
            return new JsonReturn(employeeInfo, "Data on employee not full", false);
        }
        existingEmployee.setFirstName(employeeInfo.getFirstName());
        existingEmployee.setLastName(employeeInfo.getLastName());
        existingEmployee.setPosition(employeeInfo.getPosition());
        existingEmployee.setHireDate(date);
        empRepository.save(existingEmployee);
        return new JsonReturn(existingEmployee, "", true);
    }

    public JsonReturn updateAmountLogic(Long id, JsonSalary newSalaryInfo) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn(null, "Have no employee with this id", false);
        Salary existingSalary = salRepository.findByEmployee_Id(id);
        if (newSalaryInfo.getAmount() == null)
            return new JsonReturn(null, "No amount in salary specified ", false);
        if (existingSalary != null && newSalaryInfo.getAmount().equals(existingSalary.getAmount()))
            return new JsonReturn(null, newSalaryInfo.getAmount() + " is already set as amount!", false);

        if (existingSalary == null) //if salary already set up replace old
            existingSalary = new Salary(empRepository.findById(id).get(), null, null);
        LocalDate date = null;
        try {
            date = LocalDate.parse(newSalaryInfo.getStartDate());
            existingSalary.setStartDate(date.plusDays(1));
        } catch (Exception ignored) {
        }
        if (date == null)
            existingSalary.setStartDate(LocalDate.now());
        existingSalary.setAmount(newSalaryInfo.getAmount());
        salRepository.save(existingSalary);

        List<SalaryHistory> salaryHistoryList = salHistRepository.findByEmployee_Id(id);
        salaryHistoryList.sort((o1, o2) -> {
            if (o1.getStartDate().isAfter(o2.getStartDate())) return 1;
            if (o2.getStartDate().isAfter(o1.getStartDate())) return -1;
            return 0;
        });
        if (!salaryHistoryList.isEmpty() && salaryHistoryList.get(salaryHistoryList.size() - 1).getEndDate() == null)
            salaryHistoryList.get(salaryHistoryList.size() - 1).setEndDate(LocalDate.now());
        salHistRepository.save(new SalaryHistory(empRepository.findById(id).get(), newSalaryInfo.getAmount(),
                existingSalary.getStartDate(), null));
        return new JsonReturn(newSalaryInfo.getAmount(), "", true);
    }


    public JsonReturn deleteEmployeeLogic(Long id) {
        try {
            empRepository.deleteById(id);
            return new JsonReturn(null, "", true);
        } catch (Exception e) { //todo logger
            return new JsonReturn(null, "", false);
        }
    }


}
