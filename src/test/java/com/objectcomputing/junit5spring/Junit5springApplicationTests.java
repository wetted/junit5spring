package com.objectcomputing.junit5spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectcomputing.junit5spring.model.EmployeeRepository;
import com.objectcomputing.junit5spring.rest.EmployeeRestController;
import com.objectcomputing.junit5spring.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

// uncomment to run with JUNit4 runner if the IDE doesn't have built-in JUnit5 support
//@RunWith(JUnitPlatform.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
class Junit5springApplicationTests {

    @Autowired
    EmployeeRestController controller;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("When services are injected then they should not be null")
    void contextLoads() {
        assertAll("assert that all injections are not null",
            () -> assertThat(controller, notNullValue()),
            () -> assertThat(employeeService, notNullValue()),
            () -> assertThat(employeeRepository, notNullValue()),
            () -> assertThat(objectMapper, notNullValue())
        );
	}
}
