package com.example.employee;

import com.example.employee.entities.Employee;
import com.example.employee.entities.JsonReturn;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.employee.repositories.EmpRepository;
import com.example.employee.repositories.SalHistRepository;
import com.example.employee.repositories.SalRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private SalRepository salRepository;

    @Autowired
    private SalHistRepository salHistRepository;

    @PostMapping
    public JsonReturn createEmployee(@RequestBody Employee employee) {
        if (employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() ||
                employee.getPosition().isEmpty() || employee.getHireDate() == null)
            return new JsonReturn(null, "Data on employee not full", false);
        empRepository.save(employee);
        return new JsonReturn(employee, "Data on employee not full", true);
    }

    @GetMapping
    public JsonReturn getAll() {
        List<Employee> all = empRepository.findAll();
        if (all.isEmpty())
            return new JsonReturn(all, "Employee list is empty", true);
        return new JsonReturn(all, "", true);
    }

    @GetMapping("/{id}")
    public JsonReturn getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = empRepository.findById(id);
        if (employee.isEmpty())
            return new JsonReturn(employee, "No employee with that id", false);
        return new JsonReturn(Arrays.asList(employee.get()), "", true);
    }

    @GetMapping("/")
    public JsonReturn getEmployeeByNames(@RequestParam(name = "firstName") String firstsName,
                                         @RequestParam(name = "lastName") String lastName) {
        List<Employee> employee = empRepository.findByFirstNameAndLastName(firstsName, lastName);
        if (employee.isEmpty())
            return new JsonReturn(employee, "No employee with that name and last name", false);
        return new JsonReturn(employee, "", true);
    }

    @GetMapping("/{id}/date={dateString}")
    public JsonReturn getSalaryOnDate(@PathVariable Long id, @PathVariable String dateString) {
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


    @GetMapping("/{id}/salary")
    public JsonReturn getSalary(@PathVariable Long id) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn(null, "No employee with that ID", false);
        Salary salary = salRepository.findByEmployee_Id(id);
        if (salary == null) // in case selected date before hiring
            return new JsonReturn(null, "Salary still wasn't set for that employee", false);
        return new JsonReturn(salary.getAmount().toString(), "", true);
    }

    @PutMapping("/{id}")
    public JsonReturn updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = empRepository.findById(id).get();
        if (employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() ||
                employee.getPosition().isEmpty() || employee.getHireDate() == null)
            return new JsonReturn(null, "New data on employee not full", false);
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setHireDate(employee.getHireDate());
        empRepository.save(existingEmployee);
        return new JsonReturn(existingEmployee, "", true);
    }

    @PutMapping("/{id}/salary")
    public JsonReturn updateAmount(@PathVariable Long id, @RequestBody Salary newSalary) {
        if (empRepository.findById(id).isEmpty())
            return new JsonReturn(null, "Have no employee with this id", false);
        Salary existingSalary = salRepository.findByEmployee_Id(id);
        if (newSalary.getAmount() == null)
            return new JsonReturn(null, "No amount in salary specified ", false);
        if (existingSalary != null && newSalary.getAmount().equals(existingSalary.getAmount()))
            return new JsonReturn(null, newSalary.getAmount() + " is already set as amount!", false);
        if (newSalary.getStartDate() == null)
            newSalary.setStartDate(LocalDate.now());
        if (existingSalary == null) //if salary already set up replace old
            existingSalary = new Salary(empRepository.findById(id).get(), null, null);
        existingSalary.setAmount(newSalary.getAmount());
        existingSalary.setStartDate(newSalary.getStartDate().plusDays(1));
        salRepository.save(existingSalary);
        List<SalaryHistory> hist = salHistRepository.findByEmployee_Id(id);
        if (!hist.isEmpty())
            for (SalaryHistory histPoint : hist)
                if (histPoint.getEndDate() == null)
                    histPoint.setEndDate(LocalDate.now());

        salHistRepository.save(new SalaryHistory(empRepository.findById(id).get(), newSalary.getAmount(), newSalary.getStartDate(), null));
        return new JsonReturn(newSalary.getAmount(), "", true);
    }


    @DeleteMapping("/{id}")
    public JsonReturn deleteEmployee(@PathVariable Long id) {
        try {
            empRepository.findById(id).get();
            empRepository.deleteById(id);
            return new JsonReturn(null, "", true);
        } catch (Exception e) {
            return new JsonReturn(null, "", false);
        }
    }
}