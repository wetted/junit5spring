package com.objectcomputing.junit5spring.rest;

import com.objectcomputing.junit5spring.NotFoundException;
import com.objectcomputing.junit5spring.model.Employee;
import com.objectcomputing.junit5spring.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/api/v1")
public class EmployeeRestController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRestController.class);

    private final EmployeeService employeeService;

    @ResponseStatus(value=NOT_FOUND, reason="Resource not found")
    @ExceptionHandler({
        NotFoundException.class
    })
    void handleNotFoundResponse(NotFoundException e) {
        logger.error("Status = {}, Message = {}", NOT_FOUND, e.getMessage());
        logger.debug("Status = {}, Message = {}", NOT_FOUND, e);
    }

    @Autowired
    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(
        value="/employees",
        produces = { APPLICATION_JSON_UTF8_VALUE }
    )
    public Collection<Employee> getAllEmployees() {
        logger.info("Called REST endpoint: GET /api/v1/emploxyees");
        return employeeService.getAllEmployees();
    }

    @PostMapping(
        value="/employees",
        consumes = { APPLICATION_JSON_UTF8_VALUE }
    )
    @ResponseStatus(CREATED)
    public Employee createEmployee(@RequestBody Employee employee) {
        logger.info("Called REST endpoint: POST /api/v1/employees with body {}", employee);
        return employeeService.save(employee);
    }
}
