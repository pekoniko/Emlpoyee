package com.example.employee;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonReturn;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import com.example.employee.repositories.EmpRepository;
import com.example.employee.repositories.SalHistRepository;
import com.example.employee.repositories.SalRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmpRepository empRepository;
    @Autowired
    private SalRepository salRepository;
    @Autowired
    private SalHistRepository salHistRepository;

    private static long testId;

    @BeforeAll
    void init() {
        Employee employee = new Employee("TestName", "Testovich", "pos",
                LocalDate.parse("2000-01-01"));
        Employee emp = empRepository.save(employee);
        testId = emp.getId();
        Salary salary = new Salary(testId, new BigDecimal(300), LocalDate.parse("2020-01-01"));
        salRepository.save(salary);
        SalaryHistory salaryHistory1 = new SalaryHistory(testId, new BigDecimal(300),
                Date.valueOf("2020-01-01"), Date.valueOf("2021-01-01"));
        SalaryHistory salaryHistory2 = new SalaryHistory(testId, new BigDecimal(300),
                Date.valueOf("2021-01-02"), Date.valueOf("2022-01-01"));
        SalaryHistory salaryHistory3 = new SalaryHistory(testId, new BigDecimal(300),
                Date.valueOf("2022-01-02"), null);
        salHistRepository.save(salaryHistory1);
        salHistRepository.save(salaryHistory2);
        salHistRepository.save(salaryHistory3);
    }

    @AfterAll
    void end() {
        empRepository.deleteById(testId);
        Salary salary = salRepository.findByEmployeeId(testId);
        salRepository.delete(salary);
       List<SalaryHistory> salaryHistory = salHistRepository.findByEmployeeId(testId);
        salHistRepository.deleteAll(salaryHistory);
    }


    @Test
    void createEmployee() {
        Employee employee = new Employee("Name", "LastName", "pos", LocalDate.parse("2000-01-01"));
        JsonReturn result = employeeController.createEmployee(new JsonEmployee(employee));
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNotNull(result.getResult().get("employee"));
    }

    @Test
    void createEmployee_wrongDate() {
        Employee employee = new Employee("Name", "LastName", "pos", LocalDate.parse("01-13-20545"));
        JsonReturn result = employeeController.createEmployee(new JsonEmployee(employee));
        Assertions.assertNull(result.getResult());
    }

    @Test
    void createEmployee_wrongData() {
        Employee employee = new Employee("Name", null, "pos", LocalDate.parse("2000-01-01"));
        JsonReturn result = employeeController.createEmployee(new JsonEmployee(employee));
        Assertions.assertNull(result.getResult());
    }

    @Test
    void getAll() {
        JsonReturn result = employeeController.getAll();
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNotNull(result.getResult().get("employees"));
    }

    @Test
    void getEmployeeById() {
        List<Employee> before = empRepository.findByFirstNameAndLastName("Name", "LastName");
        Assertions.assertNotNull(before);
        Assertions.assertFalse(before.isEmpty());
        JsonReturn after = employeeController.getEmployeeById(before.get(0).getId());
        Assertions.assertNotNull(after.getResult());
        Assertions.assertEquals(before, after.getResult());
    }

    @Test
    void getEmployeeByNames() {
        List<Employee> before = empRepository.findByFirstNameAndLastName("Name", "LastName");
        Assertions.assertNotNull(before);
        Assertions.assertFalse(before.isEmpty());
        JsonReturn after = employeeController.getEmployeeByNames("Name", "LastName");
        Assertions.assertNotNull(after.getResult());
        Assertions.assertEquals(before, after.getResult());
    }

    @Test
    void getSalaryOnDate() {
        List<Employee> before = empRepository.findByFirstNameAndLastName("Name", "LastName");
        Assertions.assertNotNull(before);
        Assertions.assertFalse(before.isEmpty());

    }

    @Test
    void getSalary() {
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void updateAmount() {
    }

    @Test
    void deleteEmployee() {
    }
}