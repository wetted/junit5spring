package com.objectcomputing.junit5spring.service;

import com.objectcomputing.junit5spring.model.Employee;

import java.util.List;

public interface EmployeeService {

    Employee getEmployeeById(Long id);

    Employee getEmployeeByName(String name);

    List<Employee> getAllEmployees();

    boolean exists(String email);

    Employee save(Employee employee);
}
