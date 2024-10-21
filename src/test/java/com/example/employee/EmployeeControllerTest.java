package com.example.employee;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonReturn;
import com.example.employee.dto.JsonSalary;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Salary;
import com.example.employee.entities.SalaryHistory;
import com.example.employee.repositories.EmployeeRepository;
import com.example.employee.repositories.SalaryHistoryRepository;
import com.example.employee.repositories.SalaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private SalaryRepository salaryRepository;
    @Autowired
    private SalaryHistoryRepository salaryHistoryRepository;

    private Employee employeeGlobal = null;

    @BeforeEach
    public void createTestEmployee() {
        Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
        employeeRepository.save(employee);
        employeeGlobal = employee;
    }

    @AfterEach
    public void deleteTestEmployee() {
        employeeRepository.deleteAll();
    }


    @Test
    void createEmployee() throws Exception {
        Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
        JsonEmployee jsonEmployee = new JsonEmployee(employee);
        String request = "/employee";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonEmployee)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.success());
        Assertions.assertNotNull(jsonReturn.result());
        JsonEmployee employeeReturn = objectMapper.convertValue(jsonReturn.result().get("employee"), JsonEmployee.class);
        Assertions.assertEquals(employeeReturn.firstName(), employee.getFirstName());
        Assertions.assertEquals(employeeReturn.lastName(), employee.getLastName());
    }

    @Test
    void deleteEmployeeById() throws Exception {
        Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
        employeeRepository.save(employee);
        String request = "/employee/" + employee.getId();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.success());
        Optional<Employee> foundedEmployee = employeeRepository.findById(employee.getId().longValue());
        Assertions.assertTrue(foundedEmployee.isEmpty());
    }


    @Test
    void readEmployeeById() throws Exception {
        String request = "/employee/" + employeeGlobal.getId();
        MvcResult result = getRequest(request);
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.success());
        Assertions.assertNotNull(jsonReturn.result());
        JsonEmployee employeeReturn = objectMapper.convertValue(jsonReturn.result().get("employee"), JsonEmployee.class);
        Assertions.assertEquals(employeeReturn.id(), employeeGlobal.getId());
    }

    @Test
    void updateEmployeeById() throws Exception {
        String newPosition = "posUpdated";
        Employee employee = new Employee("111test111", "222test222", newPosition, LocalDate.parse("2020-01-01"));
        String request = "/employee/" + employeeGlobal.getId();
        MvcResult result = putRequest(request, objectMapper.writeValueAsString(employee));
        var jsonReturn =
                objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.success());
        Assertions.assertNotNull(jsonReturn.result());
        JsonEmployee employeeReturn = objectMapper.convertValue(jsonReturn.result().get("employee"), JsonEmployee.class);
        Assertions.assertEquals(employeeReturn.position(), newPosition);
    }

    @Test
    void setSalary() throws Exception {
        BigDecimal amount = new BigDecimal("300");
        JsonSalary jsonSalary =
                new JsonSalary(employeeGlobal.getId(), amount, LocalDate.parse("2020-01-01"));
        String request = "/employee/" + employeeGlobal.getId() + "/salary";
        MvcResult result = putRequest(request, objectMapper.writeValueAsString(jsonSalary));
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertNotNull(jsonReturn.result().get("amount"));
        Assertions.assertEquals(String.valueOf(amount), jsonReturn.result().get("amount"));
    }

    @Test
    void updateSalary() throws Exception {
        BigDecimal newAmount = new BigDecimal("400");
        salaryRepository.save(new Salary(employeeGlobal.getId(), new BigDecimal("300"), LocalDate.parse("2021-01-01")));
        JsonSalary jsonSalary = new JsonSalary(employeeGlobal.getId(), newAmount, LocalDate.parse("2021-01-01"));
        String request = "/employee/" + employeeGlobal.getId() + "/salary";
        MvcResult result = putRequest(request, objectMapper.writeValueAsString(jsonSalary));
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertNotNull(jsonReturn.result().get("amount"));
        Assertions.assertEquals(jsonReturn.result().get("amount"), newAmount.toString());
    }

    @Test
    void getSalary() throws Exception {
        BigDecimal salaryValue = BigDecimal.valueOf(300);
        salaryRepository.save(new Salary(employeeGlobal.getId(), salaryValue, LocalDate.parse("2021-01-01")));
        String request = "/employee/" + employeeGlobal.getId() + "/salary";
        MvcResult result = getRequest(request);
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.success());
        Assertions.assertNotNull(jsonReturn.result());
        var salary = objectMapper.convertValue(jsonReturn.result().get("salary"), JsonSalary.class);
        Assertions.assertNotNull(salary.amount());
        Assertions.assertEquals(salary.amount().toString(), salaryValue.toString());
    }

    @Test
    void getSalaryOnDate() throws Exception {
        salaryHistoryRepository.save(new SalaryHistory(employeeGlobal.getId(), BigDecimal.valueOf(300),
                Date.valueOf("2020-01-01"), Date.valueOf("2020-12-31")));
        salaryHistoryRepository.save(new SalaryHistory(employeeGlobal.getId(), BigDecimal.valueOf(400),
                Date.valueOf("2021-01-01"), null));
        salaryRepository.save(new Salary(employeeGlobal.getId(), BigDecimal.valueOf(400), LocalDate.parse("2021-01-01")));
        String request = "/employee/" + employeeGlobal.getId() + "/salary?date=2020-05-05";
        MvcResult result = getRequest(request);
        var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.success());
        Assertions.assertNotNull(jsonReturn.result());
        Assertions.assertNotNull(jsonReturn.result().get("amount"));
    }

    private MvcResult getRequest(String request) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(" ")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

    }

    private MvcResult putRequest(String request, String body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}
