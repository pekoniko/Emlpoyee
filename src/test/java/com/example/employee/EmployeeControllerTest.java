package com.example.employee;

import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonReturn;
import com.example.employee.dto.JsonSalary;
import com.example.employee.entities.Employee;
import com.example.employee.repositories.EmpRepository;
import com.example.employee.repositories.SalHistRepository;
import com.example.employee.repositories.SalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpRepository empRepository;
    @Autowired
    private SalRepository salRepository;
    @Autowired
    private SalHistRepository salHistRepository;


    @Test
    @Order(1)
    void createEmployee() throws Exception {
        Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
        JsonEmployee jsonEmployee = new JsonEmployee(employee);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonEmployee)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonReturn<String> jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertNotNull(jsonReturn.getResult());
        Assertions.assertNotNull(jsonReturn.getResult().get("employeeId"));
    }


    @Test
    @Order(2)
    void readEmployeeById() throws Exception {
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + originEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonReturn<JsonEmployee> jsonReturn =
                objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertNotNull(jsonReturn.getResult());
        Assertions.assertNotNull(jsonReturn.getResult().get("employee"));
        JsonEmployee employee = objectMapper.convertValue(jsonReturn.getResult().get("employee"), JsonEmployee.class);
        Assertions.assertEquals(employee.getId(), originEmployee.getId());
    }

    @Test
    @Order(3)
    void updateEmployeeById() throws Exception {
        String newPosition = "posUpdated";
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        Employee employee = new Employee("111test111", "222test222", newPosition, LocalDate.parse("2020-01-01"));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/employee/" +
                                originEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonReturn<JsonEmployee> jsonReturn =
                objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertNotNull(jsonReturn.getResult());
        Assertions.assertNotNull(jsonReturn.getResult().get("employee"));
        JsonEmployee updatedEmployee =
                objectMapper.convertValue(jsonReturn.getResult().get("employee"), JsonEmployee.class);
        Assertions.assertNotEquals(updatedEmployee.getPosition(), originEmployee.getPosition());
        Assertions.assertEquals(updatedEmployee.getPosition(), newPosition);
    }

    @Test
    @Order(4)
    void setSalary() throws Exception {
        Long amount = 300L;
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        JsonSalary jsonSalary =
                new JsonSalary(originEmployee.getId().toString(), BigDecimal.valueOf(amount), "2020-01-01");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/employee/" +
                                originEmployee.getId() + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonSalary)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonReturn<String> jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assumptions.assumeTrue(jsonReturn.isSuccess());
        Assertions.assertNotNull(jsonReturn.getResult());
        Assertions.assertNotNull(jsonReturn.getResult().get("amount"));
        String updatedAmount = objectMapper.convertValue(jsonReturn.getResult().get("amount"), String.class);
        Assertions.assertEquals(amount.toString(), updatedAmount);
    }

    @Test
    @Order(5)
    void updateSalary() throws Exception {
        long amount = 400L;
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        JsonSalary jsonSalary = new JsonSalary(originEmployee.getId().toString(), BigDecimal.valueOf(amount), "2021-01-01");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/employee/" + originEmployee.getId() + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonSalary)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonReturn<String> jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assumptions.assumeTrue(jsonReturn.isSuccess());
        Assertions.assertNotNull(jsonReturn.getResult());
        Assertions.assertNotNull(jsonReturn.getResult().get("amount"));
        String updatedAmount = objectMapper.convertValue(jsonReturn.getResult().get("amount"), String.class);
        Assertions.assertEquals(Long.toString(amount), updatedAmount);
    }

    @Test
    @Order(6)
    void getSalary() throws Exception {
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        MvcResult result =
                mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + originEmployee.getId() +
                                        "/salary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(" ")))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        JsonReturn<String> jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assumptions.assumeTrue(jsonReturn.isSuccess());
        Assertions.assertNotNull(jsonReturn.getResult());
        JsonSalary salary = objectMapper.convertValue(jsonReturn.getResult().get("salary"), JsonSalary.class);
        Assertions.assertNotNull(salary.getAmount());
    }

    @Test
    @Order(7)
    void getSalaryOnDate() throws Exception {
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        MvcResult result =
                mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + originEmployee.getId() +
                                        "/salary/?date=2020-05-05")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(" ")))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        JsonReturn<String> jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assumptions.assumeTrue(jsonReturn.isSuccess());
        Assertions.assertNotNull(jsonReturn.getResult());
        Assertions.assertNotNull(jsonReturn.getResult().get("amount"));
    }

    @Test
    @Order(8)
    void deleteEmployeeById() throws Exception {
        String newPosition = "posUpdated";
        Employee originEmployee = empRepository.findByFirstNameAndLastName("111test111", "222test222").get(0);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/employee/" + originEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(" ")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        JsonReturn<JsonEmployee> jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
        Assertions.assertTrue(jsonReturn.isSuccess());
        Optional<Employee> employee = empRepository.findById(originEmployee.getId());
        Assertions.assertTrue(employee.isEmpty());
    }
}