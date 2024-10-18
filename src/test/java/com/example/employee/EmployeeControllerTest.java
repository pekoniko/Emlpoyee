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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Nested
    public class onlyAfterBlock {
        @Test
        void createEmployee() throws Exception {
            Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
            JsonEmployee jsonEmployee = new JsonEmployee(employee);
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/employee")
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
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/employee/" + employee.getId()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertTrue(jsonReturn.success());
            Optional<Employee> foundedEmployee = employeeRepository.findById(employee.getId());
            Assertions.assertTrue(foundedEmployee.isEmpty());
        }

        @AfterEach
        void deleteCreatedEmployee() {
            Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
            employeeRepository.delete(employee);
        }
    }
    @Nested
    public class beforeAndAfterBlock {
        private Employee employeeGlobal = null;

        @BeforeEach
        public void createTestEmployee() {
            Employee employee = new Employee("111test111", "222test222", "pos", LocalDate.parse("2020-01-01"));
            employeeRepository.save(employee);
            employeeGlobal = employee;
        }

        @AfterEach
        public void deleteTestEmployee() {
            employeeRepository.delete(employeeGlobal);
        }

        @Test
        void readEmployeeById() throws Exception {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + employeeGlobal.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString("")))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            var jsonReturn =
                    objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertTrue(jsonReturn.success());
            Assertions.assertNotNull(jsonReturn.result());
            JsonEmployee employeeReturn = objectMapper.convertValue(jsonReturn.result().get("employee"), JsonEmployee.class);
            Assertions.assertEquals(employeeReturn.id(), employeeGlobal.getId());
        }

        @Test
        void updateEmployeeById() throws Exception {
            String newPosition = "posUpdated";
            Employee employee = new Employee("111test111", "222test222", newPosition, LocalDate.parse("2020-01-01"));
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/employee/" +
                                    employeeGlobal.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(employee)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            var jsonReturn =
                    objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertTrue(jsonReturn.success());
            Assertions.assertNotNull(jsonReturn.result());
            JsonEmployee employeeReturn = objectMapper.convertValue(jsonReturn.result().get("employee"), JsonEmployee.class);
            Assertions.assertEquals(employeeReturn.position(), newPosition);
        }

        @Test
        void setSalary() throws Exception {
            long amount = 300L;
            JsonSalary jsonSalary =
                    new JsonSalary(employeeGlobal.getId(), BigDecimal.valueOf(amount), LocalDate.parse("2020-01-01"));
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/employee/" +
                                    employeeGlobal.getId() + "/salary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(jsonSalary)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertNotNull(jsonReturn.result().get("amount"));
            Assertions.assertEquals(Long.toString(amount), jsonReturn.result().get("amount"));
        }

        @Test
        void updateSalary() throws Exception {
            long newAmount = 400L;
            salaryRepository.save(new Salary(employeeGlobal.getId(), BigDecimal.valueOf(300L), LocalDate.parse("2021-01-01")));
            JsonSalary jsonSalary = new JsonSalary(employeeGlobal.getId(), BigDecimal.valueOf(newAmount), LocalDate.parse("2021-01-01"));
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/employee/" + employeeGlobal.getId() + "/salary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(jsonSalary)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andReturn();
            var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertNotNull(jsonReturn.result().get("amount"));
            Assertions.assertEquals(jsonReturn.result().get("amount"), Long.toString(newAmount));
        }

        @Test
        void getSalary() throws Exception {
            long salaryValue = 300L;
            salaryRepository.save(new Salary(employeeGlobal.getId(), BigDecimal.valueOf(salaryValue), LocalDate.parse("2021-01-01")));
            MvcResult result =
                    mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + employeeGlobal.getId() +
                                            "/salary")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(" ")))
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andReturn();
            var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertTrue(jsonReturn.success());
            Assertions.assertNotNull(jsonReturn.result());
            var salary = objectMapper.convertValue(jsonReturn.result().get("salary"), JsonSalary.class);
            Assertions.assertNotNull(salary.amount());
            Assertions.assertEquals(salary.amount().toString(), Long.toString(salaryValue));
        }

        @Test
        void getSalaryOnDate() throws Exception {
            salaryHistoryRepository.save(new SalaryHistory(employeeGlobal.getId(), BigDecimal.valueOf(300L),
                    Date.valueOf("2020-01-01"), Date.valueOf("2020-12-31")));
            salaryHistoryRepository.save(new SalaryHistory(employeeGlobal.getId(), BigDecimal.valueOf(400L),
                    Date.valueOf("2021-01-01"), null));
            salaryRepository.save(new Salary(employeeGlobal.getId(), BigDecimal.valueOf(400L), LocalDate.parse("2021-01-01")));
            MvcResult result =
                    mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + employeeGlobal.getId() +
                                            "/salary?date=2020-05-05")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(" ")))
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andReturn();
            var jsonReturn = objectMapper.readValue(result.getResponse().getContentAsString(), JsonReturn.class);
            Assertions.assertTrue(jsonReturn.success());
            Assertions.assertNotNull(jsonReturn.result());
            Assertions.assertNotNull(jsonReturn.result().get("amount"));
        }
    }
}