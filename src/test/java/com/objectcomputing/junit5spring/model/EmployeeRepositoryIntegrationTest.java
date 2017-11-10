package com.objectcomputing.junit5spring.model;

import com.objectcomputing.extensions.MockitoExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isOneOf;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// uncomment to run with JUNit4 runner if the IDE doesn't have built-in JUnit5 support
//@RunWith(JUnitPlatform.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@DataJpaTest
@Tag("integration")
class EmployeeRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("when FindByName then return the employee")
    void whenFindByName_thenReturnEmployee() {
        assertThat(employeeRepository, notNullValue());
        // given
        Employee expected = new Employee("alex");
        Object id = entityManager.persistAndGetId(expected);

        // when
        Employee actual = employeeRepository.findByName(expected.getName());

        // then
        assertAll(
            () -> assertThat(actual.getName(), equalTo(expected.getName())),
            () -> assertThat(actual.getId(), equalTo(id))
        );
    }

    @Test
    @DisplayName("when FindAll then return all employees")
    void whenFindAll_thenReturnAllEmployees() {
        assertThat(employeeRepository, notNullValue());
        // given
        Employee bob = new Employee("bob");
        Employee alex = new Employee("alex");
        Employee john = new Employee("john");
        List<Employee> employees = Arrays.asList(bob, alex, john);
        employeeRepository.save(employees);
        employeeRepository.flush();

        // when
        List<Employee> allEmployees = employeeRepository.findAll();

        // then
        assertAll(
            () -> assertThat(allEmployees, hasSize(3)),
            () -> assertThat(
                allEmployees.stream().map(Employee::getName).collect(Collectors.toList()),
                containsInAnyOrder(alex.getName(), bob.getName(), john.getName())
            )
        );
    }

    @TestFactory
    @DisplayName("FindById - Dynamic Id Tests")
    Stream<DynamicTest> generateFindByIdDynamicTests() {
        assertThat(employeeRepository, notNullValue());
        // given
        Employee bob = new Employee("bob");
        Employee alex = new Employee("alex");
        Employee john = new Employee("john");
        List<Employee> employees = Arrays.asList(bob, alex, john);
        employeeRepository.save(employees);
        employeeRepository.flush();

        // when
        Long[] ids = employeeRepository.findAll().stream()
            .map(Employee::getId)
            .collect(Collectors.toList())
            .toArray(new Long[]{});

        // then
        return Stream.of(ids).map(id -> dynamicTest("Find by employee id " + id, () -> {
            Employee emp = employeeRepository.findById(id);
            assertThat(emp, notNullValue());
            assertThat(emp.getName(), isOneOf("bob","alex","john"));
        }));
    }
}

