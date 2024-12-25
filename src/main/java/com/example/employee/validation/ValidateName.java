package com.example.employee.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {NameValidation.class})
public @interface ValidateName {

    String message() default "Invalid Name: Must be of 1 - 20 letters.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}