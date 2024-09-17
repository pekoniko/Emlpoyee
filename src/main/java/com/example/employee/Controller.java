package com.example.employee;

import com.example.employee.entities.Employee;
import com.example.employee.entities.JsonReturn;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.employee.repositories.EmpRep;
import com.example.employee.repositories.SalHistRep;
import com.example.employee.repositories.SalRep;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/employee")
public class Controller {

    @Autowired
    private EmpRep empRep;

    @Autowired
    private SalRep salRep;

    @Autowired
    private SalHistRep salHistRep;

    @PostMapping
    public JsonReturn createEmployee(@RequestBody Employee employee) {
        if (employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() ||
                employee.getPosition().isEmpty() || employee.getHireDate() == null)
            return new JsonReturn(null, "Data on employee not full", false);
        empRep.save(employee);
        return new JsonReturn(employee, "Data on employee not full", true);
    }

    @GetMapping("/all")
    public JsonReturn getAll() {
        List<Employee> all = empRep.findAll();
        if (all.isEmpty())
            return new JsonReturn(all, "Employee list is empty", true);
        return new JsonReturn(all, "", true);
    }

    @GetMapping("/{id}")
    public JsonReturn getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = empRep.findById(id);
        if (employee.isEmpty())
            return new JsonReturn(employee, "No employee with that id", false);
        return new JsonReturn(Arrays.asList(employee.get()), "", true);
    }

    @GetMapping("/{firsts_name}/{last_name}")
    public JsonReturn getEmployeeById(@PathVariable String firsts_name, @PathVariable String last_name) {
        List<Employee> employee = empRep.findByFirstNameAndLastName(firsts_name, last_name);
        if (employee.isEmpty())
            return new JsonReturn(employee, "No employee with that name and last name", false);
        return new JsonReturn(employee, "", true);
    }

    @GetMapping("/{id}/date={dateString}")
    public JsonReturn getSalaryOnDate(@PathVariable Long id, @PathVariable String dateString) {
        if (empRep.findById(id).isEmpty())
            return new JsonReturn(null, "No employee with that ID", false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            return new JsonReturn(null, "Wrong date format", false);
        }
        List<SalaryHistory> allHistory = salHistRep.findByEmployee_Id(id);
        allHistory.sort(Comparator.comparing(SalaryHistory::getId));
        if (date.before(allHistory.get(0).getStartDate())) // in case selected date before hiring
            return new JsonReturn(null, "Date is before first record", false);
        for (SalaryHistory oneObj : allHistory)
            if (date.after(oneObj.getStartDate()) && (oneObj.getEndDate() != null || date.before(oneObj.getEndDate())))
                return new JsonReturn(oneObj.getAmount(), "", true);
        return new JsonReturn(null, "Salary not found on selected date", false);
    }

    @GetMapping("/{id}/amount")
    public JsonReturn getSalary(@PathVariable Long id) {
        if (empRep.findById(id).isEmpty())
            return new JsonReturn(null, "No employee with that ID", false);
        Salary salary = salRep.findByEmployee_Id(id);
        if (salary == null) // in case selected date before hiring
            return new JsonReturn(null, "Salary still wasn't set for that employee", false);
        return new JsonReturn(salary.getAmount().toString(), "", true);
    }

    @PutMapping("/{id}")
    public JsonReturn updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = empRep.findById(id).get();
        if (employee.getFirstName().isEmpty() || employee.getLastName().isEmpty() ||
                employee.getPosition().isEmpty() || employee.getHireDate() == null)
            return new JsonReturn(null, "New data on employee not full", false);
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setHireDate(employee.getHireDate());
        empRep.save(existingEmployee);
        return new JsonReturn(existingEmployee, "", true);
    }

    @PutMapping("/{id}/amount={amount}")
    public JsonReturn updateAmount(@PathVariable Long id, @PathVariable BigDecimal amount) {
        Salary existingSalary = salRep.findByEmployee_Id(id);
        if (existingSalary != null && existingSalary.getAmount() == amount)
            return new JsonReturn(null, amount + " value is already set as amount!", false);
        if (existingSalary == null) //if salary already set up delete old
            existingSalary = new Salary(empRep.findById(id).get(), amount, new Date());
        existingSalary.setAmount(amount);
        existingSalary.setStartDate(new Date());
        salRep.save(existingSalary);
        List<SalaryHistory> hist = salHistRep.findByEmployee_Id(id);
        if (!hist.isEmpty())
            for (SalaryHistory histPoint : hist)
                if (histPoint.getEndDate() == null)
                    histPoint.setEndDate(new Date());

        salHistRep.save(new SalaryHistory(empRep.findById(id).get(), amount, new Date(), null));
        return new JsonReturn(amount, "", true);
    }


    @DeleteMapping("/{id}")
    public JsonReturn deleteEmployee(@PathVariable Long id) {
        try {
            empRep.findById(id).get();
            empRep.deleteById(id);
            return new JsonReturn(null, "", true);
        } catch (Exception e) {
            return new JsonReturn(null, "", false);
        }
    }
}