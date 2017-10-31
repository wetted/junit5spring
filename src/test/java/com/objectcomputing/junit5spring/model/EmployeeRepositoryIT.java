package com.objectcomputing.junit5spring.model;

import com.objectcomputing.extensions.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

// uncomment to run with JUNit4 runner if the IDE doesn't have built-in JUnit5 support
//@RunWith(JUnitPlatform.class)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@DataJpaTest
@Tag("integration")
class EmployeeRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("when FindByName then return Employee")
    void whenFindByName_thenReturnEmployee() {
        assertThat(employeeRepository, notNullValue());
        // given
        Employee expected = new Employee("alex");
        entityManager.persist(expected);
        entityManager.flush();
        // when
        Employee actual = employeeRepository.findByName(expected.getName());
        // then
        assertThat(actual.getName(), equalTo(expected.getName()));
    }
}