package com.example.employee.controller;

import com.example.employee.service.EmployeeService;
import com.example.employee.dto.JsonEmployee;
import com.example.employee.dto.JsonSalary;
import com.example.employee.dto.JsonReturn;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @Operation(summary = "Create new employee",
            description = "All parameters except id must be presented into enter json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}
    )
    @PostMapping
    public JsonReturn createEmployee(@Validated(JsonEmployee.OnCreate.class) @RequestBody JsonEmployee employee) {
        return service.createEmployee(employee);
    }


    @Operation(summary = "Get employees by parameters",
            description = "Get list of employees by chosen parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}
    )
    @GetMapping()
    public JsonReturn getAll(@RequestParam(name = "firstName", required = false) String firstName,
                             @RequestParam(name = "lastName", required = false) String lastName,
                             @RequestParam(name = "position", required = false) String position
    ) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("position", position);
        return service.getAll(params);
    }


    @Operation(summary = "Get translation of position",
            description = "Get translation of position using yandex api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}
    )
    @GetMapping("/{id}/position")
    public JsonReturn getTranslatedPosition(@PathVariable Long id,
                                            @RequestParam(name = "language") String language
    ) {
        return service.getTranslatedPosition(id, language);
    }


    @Operation(summary = "Get employee by Id",
            description = "Get employee info from table employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}
    )
    @GetMapping("/{id}")
    public JsonReturn getEmployeeById(@PathVariable Long id) {
        return service.getEmployeeById(id);
    }


    @Operation(summary = "Get employee salary on date",
            description = "Get employee salary on selected date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )})
    @GetMapping(value = "/{id}/salary", params = {"date"})
    public JsonReturn getSalaryOnDate(@PathVariable Long id, @RequestParam(name = "date") String dateString) {
        return service.getSalaryOnDate(id, dateString);
    }


    @Operation(summary = "Get employee salary",
            description = "Get employee salary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )})
    @GetMapping(value = "/{id}/salary")
    public JsonReturn getSalary(@PathVariable Long id) {
        return service.getSalary(id);
    }


    @Operation(summary = "Put new info about employee",
            description = "Update employee info in table employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            ),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}

    )
    @PutMapping("/{id}")
    public JsonReturn updateEmployee(@PathVariable Long id, @Validated @RequestBody JsonEmployee employee) {
        return service.updateEmployee(id, employee);
    }


    @Operation(summary = "Put new info about employee salary",
            description = "Update employee salary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}
    )
    @PutMapping("/{id}/salary")
    public JsonReturn updateAmount(@PathVariable Long id, @Valid @RequestBody JsonSalary newSalary) {
        return service.updateAmount(id, newSalary);
    }


    @Operation(summary = "Delete employee",
            description = "Delete employee by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}),
            @ApiResponse(responseCode = "400",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JsonReturn.class))}

            )}
    )
    @DeleteMapping("/{id}")
    public JsonReturn deleteEmployee(@PathVariable Long id) {
        return service.deleteEmployeeById(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonReturn handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ObjectMapper map = new ObjectMapper();
        return new JsonReturn<>(null, errors.toString(), false);
//        return errors;
    }
}