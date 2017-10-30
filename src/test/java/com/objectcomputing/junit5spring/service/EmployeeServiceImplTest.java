package com.objectcomputing.junit5spring.service;

import com.objectcomputing.extensions.MockitoExtension;
import com.objectcomputing.junit5spring.model.Employee;
import com.objectcomputing.junit5spring.model.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

// uncomment to run with JUNit4 runner if the IDE doesn't have built-in JUnit5 support
//@RunWith(JUnitPlatform.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        Employee john = new Employee("john");
        john.setId(11L);
        Employee bob = new Employee("bob");
        Employee alex = new Employee("alex");

        List<Employee> allEmployees = Arrays.asList(john, bob, alex);

        when(employeeRepository.findByName(john.getName())).thenReturn(john);
        when(employeeRepository.findByName(alex.getName())).thenReturn(alex);
        when(employeeRepository.findByName("wrong_name")).thenReturn(null);
        when(employeeRepository.findById(john.getId())).thenReturn(john);
        when(employeeRepository.findAll()).thenReturn(allEmployees);
        when(employeeRepository.findById(-99L)).thenReturn(null);
    }

    @Test
    @DisplayName("when valid Name then Employee should be found")
    void whenValidName_thenEmployeeShouldBeFound() {
        String name = "alex";
        Employee found = employeeService.getEmployeeByName(name);
        assertAll(
            () -> assertThat(found.getName(), equalTo(name)),
            () -> verifyFindByNameIsCalledOnce("alex")
        );

    }

    @Test
    @DisplayName("when invalid Name then Employee should not be found")
    void whenInValidName_thenEmployeeShouldNotBeFound() {
        Employee fromDb = employeeService.getEmployeeByName("wrong_name");
        assertAll(
            () -> assertThat(fromDb, nullValue()),
            () -> verifyFindByNameIsCalledOnce("wrong_name")
        );
    }

    @Test
    @DisplayName("when valid Name then Employee should exist")
    void whenValidName_thenEmployeeShouldExist() {
        boolean doesEmployeeExist = employeeService.exists("john");
        assertAll(
            () -> assertThat(doesEmployeeExist, equalTo(true)),
            () -> verifyFindByNameIsCalledOnce("john")
        );
    }

    @Test
    @DisplayName("when non existing Name then Employee should not exist")
    void whenNonExistingName_thenEmployeeShouldNotExist() {
        boolean doesEmployeeExist = employeeService.exists("some_name");
        assertAll(
            () -> assertThat(doesEmployeeExist, equalTo(false)),
            () -> verifyFindByNameIsCalledOnce("some_name")
        );
    }

    @Test
    @DisplayName("when valid Id then Employee should be found")
    void whenValidId_thenEmployeeShouldBeFound() {
        Employee fromDb = employeeService.getEmployeeById(11L);
        assertAll(
            () -> assertThat(fromDb.getName(), equalTo("john")),
            this::verifyFindByIdIsCalledOnce
        );
    }

    @Test
    @DisplayName("when invalid Id then Employee should not be found")
    void whenInValidId_thenEmployeeShouldNotBeFound() {
        Employee fromDb = employeeService.getEmployeeById(-99L);
        assertAll(
            () -> assertThat(fromDb, nullValue()),
            this::verifyFindByIdIsCalledOnce
        );
    }

    @Test
    @DisplayName("given 3 Employees when getAll then return 3 records")
    void given3Employees_whengetAll_thenReturn3Records() {
        Employee alex = new Employee("alex");
        Employee john = new Employee("john");
        Employee bob = new Employee("bob");

        List<Employee> allEmployees = employeeService.getAllEmployees();

        assertAll(
            "verify returned collection",
            this::verifyFindAllEmployeesIsCalledOnce,
            () -> assertThat(allEmployees, hasSize(3)),
            () -> assertThat(
                    allEmployees.stream().map(Employee::getName).collect(Collectors.toList()),
                    containsInAnyOrder(alex.getName(), john.getName(), bob.getName())
                    )
        );

        // this is how AssertJ works for the last two asserts above
//        assertThat(allEmployees).hasSize(3)
//            .extracting(Employee::getName)
//            .contains(alex.getName(), john.getName(), bob.getName());
    }

    private void verifyFindByNameIsCalledOnce(String name) {
        verify(employeeRepository, VerificationModeFactory.times(1))
            .findByName(name);
        reset(employeeRepository);
    }

    private void verifyFindByIdIsCalledOnce() {
        verify(employeeRepository, VerificationModeFactory.times(1))
            .findById(Mockito.anyLong());
        reset(employeeRepository);
    }

    private void verifyFindAllEmployeesIsCalledOnce() {
        verify(employeeRepository, VerificationModeFactory.times(1))
            .findAll();
        reset(employeeRepository);
    }
}
