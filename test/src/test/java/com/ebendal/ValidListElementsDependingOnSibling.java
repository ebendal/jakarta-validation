package com.ebendal;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidListElementsDependingOnSibling.Validator.class)
public @interface ValidListElementsDependingOnSibling {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidListElementsDependingOnSibling, ValidatedObject> {

        @Override
        public boolean isValid(ValidatedObject input, ConstraintValidatorContext context) {
            if (input == null || input.getListElements() == null) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            boolean result = true;
            for (int i = 0; i < input.getListElements().size(); i++) {
                if (input.getListElements().get(i).equalsIgnoreCase("illegal") && !input.isIllegalListElementsAllowed()) {
                    addConstraintViolation(context, i);
                    result = false;
                }
            }
            return result;
        }

        private void addConstraintViolation(ConstraintValidatorContext context, int i) {
            context.buildConstraintViolationWithTemplate("element has illegal value")
                .addPropertyNode("listElements")
                .addBeanNode()
                .inIterable()
                .atIndex(i)
                .addConstraintViolation();
        }
    }
}
