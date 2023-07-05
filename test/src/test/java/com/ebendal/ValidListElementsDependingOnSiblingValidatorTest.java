package com.ebendal;

import com.ebendal.jakarta.validation.CustomConstraintValidatorTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidListElementsDependingOnSiblingValidatorTest extends CustomConstraintValidatorTest {

    @InjectMocks
    private ValidListElementsDependingOnSibling.Validator sut;

    @Test
    void isValid_illegalValueInList_illegalValuesNotAllowed_violation() {
        var input = ValidatedObject.builder()
            .listElements(List.of("", "illegal", ""))
            .illegalListElementsAllowed(false)
            .build();

        boolean result = sut.isValid(input, context);

        assertThat(result).isFalse();
        verifyCustomConstraintViolation("element has illegal value", subPath("listElements", 1));
    }

    @Test
    void isValid_illegalValueInList_illegalValuesAllowed_noViolation() {
        var input = ValidatedObject.builder()
            .listElements(List.of("", "illegal", ""))
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
    void isValid_listElementsNull_noViolation() {
        var input = ValidatedObject.builder()
            .listElements(null)
            .illegalListElementsAllowed(false)
            .build();

        boolean result = sut.isValid(input, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_noIllegalElements_noViolation() {
        var input = ValidatedObject.builder()
            .listElements(List.of(""))
            .illegalListElementsAllowed(false)
            .build();

        boolean result = sut.isValid(input, context);

        assertThat(result).isTrue();
    }
}
