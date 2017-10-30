package com.objectcomputing.junit5spring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.junit5spring.data.Employee;
import com.objectcomputing.junit5spring.data.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection.H2;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = H2)
//@AutoConfigureRestDocs(outputDir = "target/rest-docs")
class EmployeeRestControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository repository;

    @AfterEach
    void resetDb() {
        repository.deleteAll();
    }

    @Test
    @Tag("integration")
    @DisplayName("when valid input then create employee with HTTP status = 201")
    void whenValidInput_thenCreateEmployee() throws Exception {
        Employee bob = new Employee("bob");
        mvc.perform(post("/api/v1/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bob)))
            .andDo(print())
            .andExpect(status().isCreated()
            );

        List<Employee> employeesFound = repository.findAll();
        assertAll(
            () -> assertThat(employeesFound.size(), equalTo(1)),
            () -> assertThat(
                    employeesFound.stream().map(Employee::getName).collect(Collectors.toList()),
                    hasItem(bob.getName())
                )
        );
    }

    @Test
    @Tag("integration")
    @DisplayName("given Employees when GET /employees then HTTP status = 200")
    void givenEmployees_whenGetEmployees_thenStatus200() throws Exception {

        Employee bob = new Employee("bob");
        Employee alex = new Employee("alex");
        repository.save(Arrays.asList(bob, alex));
        repository.flush();

        mvc.perform(get("/api/v1/employees").contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
            .andExpect(jsonPath("$[0].name", is("bob")))
            .andExpect(jsonPath("$[1].name", is("alex")));
    }
}