package com.example.employee;

import com.example.employee.entities.Employee;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/employees")
public class Controller {

    @Autowired
    private EmpRep empRep;

    @Autowired
    private SalRep salRep;

    @Autowired
    private SalHistRep salHistRep;

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return empRep.save(employee);
    }

    @GetMapping("/all")
    public List<Employee> getAll() {
        return empRep.findAll();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return empRep.findById(id).get();
    }

    @GetMapping("/{firsts_name}/{last_name}")
    public List<Employee> getEmployeeById(@PathVariable String firsts_name, @PathVariable String last_name) {
        return empRep.findByFirstNameAndLastName(firsts_name, last_name);
    }

    @GetMapping("/{id}/date={dateString}")
    public String getSalaryOnDate(@PathVariable Long id, @PathVariable String dateString) {
        if (empRep.findById(id).isEmpty())
            return "No employee with that ID";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        List<SalaryHistory> allHistory = salHistRep.findByEmployee_Id(id);
        allHistory.sort(Comparator.comparing(SalaryHistory::getId));
        if (date.before(allHistory.get(0).getStartDate())) // in case selected date before hiring
            return "Date is before first record";
        for (SalaryHistory oneObj : allHistory)
            if (date.after(oneObj.getStartDate()) && (oneObj.getEndDate() != null || date.before(oneObj.getEndDate())))
                return String.valueOf(oneObj.getAmount());
        return "Some new mistake&"; // in case selected date wasn't found
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existingEmployee = empRep.findById(id).get();
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setHireDate(employee.getHireDate());
        return empRep.save(existingEmployee);
    }

    @PutMapping("/{id}/amount={amount}")
    public String updateAmount(@PathVariable Long id, @PathVariable double amount) {
        Salary existingSalary = salRep.findByEmployee_Id(id);
        if (existingSalary != null && existingSalary.getAmount() == amount)
            return amount + " value is already set as amount!";
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
        return amount + " successfully set as payment.";
    }


    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        try {
            empRep.findById(id).get();
            empRep.deleteById(id);
            return "Employee deleted successfully";
        } catch (Exception e) {
            return "Employee not found";
        }
    }
}