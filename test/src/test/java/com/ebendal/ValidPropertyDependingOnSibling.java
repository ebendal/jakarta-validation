package com.ebendal;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPropertyDependingOnSibling.Validator.class)
public @interface ValidPropertyDependingOnSibling {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidPropertyDependingOnSibling, ValidatedObject> {

        @Override
        public boolean isValid(ValidatedObject input, ConstraintValidatorContext context) {
            if (input == null || input.getProperty() == null) {
                return true;
            }
            if (input.getProperty().equalsIgnoreCase("illegal") && !input.isIllegalListElementsAllowed()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("property has illegal value")
                    .addPropertyNode("property")
                    .addConstraintViolation();
                return false;
            }
            return true;
        }

    }
}
