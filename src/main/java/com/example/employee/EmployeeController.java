package com.example.employee;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonSalary;
import com.example.employee.dto.JsonReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    @Autowired
    EmployeeService service;

    @PostMapping
    public JsonReturn createEmployee(@RequestBody JsonEmployee employee) {
        return service.checkEmployeeLogic(employee);
    }

    @GetMapping
    public JsonReturn getAll() {
        return service.getAllLogic();
    }

    @GetMapping("/{id}")
    public JsonReturn getEmployeeById(@PathVariable Long id) {
        return service.getEmployeeByIdLogic(id);
    }

    @GetMapping("/")
    public JsonReturn getEmployeeByNames(@RequestParam(name = "firstName") String firstName,
                                         @RequestParam(name = "lastName") String lastName) {
        return service.getEmployeeByNamesLogic(firstName, lastName);
    }

    @GetMapping("/{id}/salary/")
    public JsonReturn getSalaryOnDate(@PathVariable Long id, @RequestParam(name = "date") String dateString) {
        return service.getSalaryOnDateLogic(id, dateString);
    }


    @GetMapping("/{id}/salary")
    public JsonReturn getSalary(@PathVariable Long id) {
        return service.getSalaryLogic(id);
    }

    @PutMapping("/{id}")
    public JsonReturn updateEmployee(@PathVariable Long id, @RequestBody JsonEmployee employee) {
        return service.updateEmployeeLogic(id, employee);
    }

    @PutMapping("/{id}/salary")
    public JsonReturn updateAmount(@PathVariable Long id, @RequestBody JsonSalary newSalary) {
        return service.updateAmountLogic(id, newSalary);
    }


    @DeleteMapping("/{id}")
    public JsonReturn deleteEmployee(@PathVariable Long id) {
      return service.deleteEmployeeLogic(id);
    }
}