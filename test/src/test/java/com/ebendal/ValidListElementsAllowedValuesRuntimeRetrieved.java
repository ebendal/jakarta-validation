package com.ebendal;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidListElementsAllowedValuesRuntimeRetrieved.Validator.class)
public @interface ValidListElementsAllowedValuesRuntimeRetrieved {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ValidListElementsAllowedValuesRuntimeRetrieved, List<String>> {

        private final AllowedValuesProvider allowedValuesProvider;

        @Override
        public boolean isValid(List<String> input, ConstraintValidatorContext context) {
            if (input == null) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            boolean result = true;
            for (int i = 0; i < input.size(); i++) {
                if (!allowedValuesProvider.getAllowedValues().contains(input.get(i))) {
                    addConstraintViolation(context, i);
                    result = false;
                }
            }
            return result;
        }

        private void addConstraintViolation(ConstraintValidatorContext context, int i) {
            context.buildConstraintViolationWithTemplate("illegal value, allowed values are: " + allowedValuesProvider.getAllowedValues().toString())
                .addBeanNode()
                .inIterable()
                .atIndex(i)
                .addConstraintViolation();
        }
    }
}
