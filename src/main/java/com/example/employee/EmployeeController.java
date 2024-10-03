package com.example.employee;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonSalary;
import com.example.employee.dto.JsonReturn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    @Autowired
    EmployeeService service;

    @Operation(summary = "Create new employee",
     description = "All parameters except id must be presented into enter json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee created or got some predicted error",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @PostMapping
    public JsonReturn createEmployee(@RequestBody JsonEmployee employee) {
        return service.checkEmployeeLogic(employee);
    }

    @Operation(summary = "Get all employees",
            description = "Get list of all employees in DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got list of all employees",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @GetMapping
    public JsonReturn getAll() {
        return service.getAllLogic();
    }

    @Operation(summary = "Get employee by Id",
            description = "Get employee info from table employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got employee or message that employee not exist",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @GetMapping("/{id}")
    public JsonReturn getEmployeeById(@PathVariable Long id) {
        return service.getEmployeeByIdLogic(id);
    }

    @Operation(summary = "Get employees by Names",
            description = "Get list of all employees by first and last name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got list of employees with selected first and" +
                    " last name or message that employee not exist",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @GetMapping("/")
    public JsonReturn getEmployeeByNames(@RequestParam(name = "firstName") String firstName,
                                         @RequestParam(name = "lastName") String lastName) {
        return service.getEmployeeByNamesLogic(firstName, lastName);
    }

    @Operation(summary = "Get employee salary on date",
            description = "Get employee salary on selected date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got salary of employee on chosen date or" +
                    "error message",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @GetMapping("/{id}/salary/")
    public JsonReturn getSalaryOnDate(@PathVariable Long id, @RequestParam(name = "date") String dateString) {
        return service.getSalaryOnDateLogic(id, dateString);
    }

    @Operation(summary = "Get employee salary",
            description = "Get employee salary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got employee salary or error message",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @GetMapping("/{id}/salary")
    public JsonReturn getSalary(@PathVariable Long id) {
        return service.getSalaryLogic(id);
    }

    @Operation(summary = "Put new info about employee",
            description = "Update employee info in table employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got changed info about chosen employee or error message",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @PutMapping("/{id}")
    public JsonReturn updateEmployee(@PathVariable Long id, @RequestBody JsonEmployee employee) {
        return service.updateEmployeeLogic(id, employee);
    }

    @Operation(summary = "Put new info about employee salary",
            description = "Update employee salary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got new employee salary or error message",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @PutMapping("/{id}/salary")
    public JsonReturn updateAmount(@PathVariable Long id, @RequestBody JsonSalary newSalary) {
        return service.updateAmountLogic(id, newSalary);
    }


    @Operation(summary = "Delete employee",
            description = "Delete employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got confirmation or error message",
                    content = {@Content(mediaType = "json",
                            schema = @Schema(implementation = JsonEmployee.class))})})
    @DeleteMapping("/{id}")
    public JsonReturn deleteEmployee(@PathVariable Long id) {
        return service.deleteEmployeeLogic(id);
    }
}