package com.objectcomputing.junit5spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.junit5spring.data.Employee;
import com.objectcomputing.junit5spring.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(controllers = EmployeeRestController.class)
@WebMvcTest
@WebAppConfiguration
class EmployeeRestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        EmployeeRestController restController = new EmployeeRestController(null);
        Object actualTarget = AopTestUtils.getUltimateTargetObject(restController);
        ReflectionTestUtils.setField(actualTarget, "employeeService", employeeService);
        mockMvc = MockMvcBuilders.standaloneSetup(restController).build();
    }

    @Test
    void whenPostEmployee_thenCreateEmployee() throws Exception {
        Employee alex = new Employee("alex");
        given(employeeService.save(any(Employee.class))).willReturn(alex);

        mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(alex)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("alex")));
        verify(employeeService, VerificationModeFactory.times(1)).save(Mockito.any());
        reset(employeeService);
    }

    @Test
    void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {
        Employee alex = new Employee("alex");
        Employee john = new Employee("john");
        Employee bob = new Employee("bob");
        List<Employee> allEmployees = Arrays.asList(alex, john, bob);

        given(employeeService.getAllEmployees()).willReturn(allEmployees);

        mockMvc.perform(get("/api/v1/employees").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].name", is(alex.getName())))
            .andExpect(jsonPath("$[1].name", is(john.getName())))
            .andExpect(jsonPath("$[2].name", is(bob.getName())));
        verify(employeeService, VerificationModeFactory.times(1)).getAllEmployees();
        reset(employeeService);
    }
}
