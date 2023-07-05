package com.ebendal;

import com.ebendal.jakarta.validation.CustomConstraintValidatorTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;

class ValidPropertyDependingOnSiblingValidatorTest extends CustomConstraintValidatorTest {

    @InjectMocks
    private ValidPropertyDependingOnSibling.Validator sut;

    @Test
    void isValid_illegalValue_illegalValuesNotAllowed_violation() {
        var input = ValidatedObject.builder()
            .property("illegal")
            .illegalListElementsAllowed(false)
            .build();

        boolean result = sut.isValid(input, context);

        assertThat(result).isFalse();
        verifyCustomConstraintViolation("property has illegal value", subPath("property"));
    }

    @Test
    void isValid_illegalValue_illegalValuesAllowed_noViolation() {
        var input = ValidatedObject.builder()
            .property("illegal")
            .illegalListElementsAllowed(true)
            .build();

        boolean result = sut.isValid(input, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_null_noViolation() {
        boolean result = sut.isValid(null, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_propertyNull_noViolation() {
        var input = ValidatedObject.builder()
            .property(null)
            .illegalListElementsAllowed(false)
            .build();

        boolean result = sut.isValid(input, context);

        assertThat(result).isTrue();
    }
}
