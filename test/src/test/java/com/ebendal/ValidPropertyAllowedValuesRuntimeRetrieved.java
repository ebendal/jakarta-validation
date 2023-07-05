package com.ebendal;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPropertyAllowedValuesRuntimeRetrieved.Validator.class)
public @interface ValidPropertyAllowedValuesRuntimeRetrieved {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ValidPropertyAllowedValuesRuntimeRetrieved, String> {

        private final AllowedValuesProvider allowedValuesProvider;

        @Override
        public boolean isValid(String input, ConstraintValidatorContext context) {
            if (input == null || allowedValuesProvider.getAllowedValues().contains(input)) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("illegal value, allowed values are: " + allowedValuesProvider.getAllowedValues().toString())
                .addConstraintViolation();
            return false;
        }

    }
}
