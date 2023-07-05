package com.ebendal;

import com.ebendal.jakarta.validation.CustomConstraintValidatorTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ValidListElementsAllowedValuesRuntimeRetrievedValidatorTest extends CustomConstraintValidatorTest {

    @Mock
    private AllowedValuesProvider allowedValuesProvider;

    @InjectMocks
    private ValidListElementsAllowedValuesRuntimeRetrieved.Validator sut;

    @Test
    void isValid_notAllowedValueInList_violation() {
        when(allowedValuesProvider.getAllowedValues()).thenReturn(Set.of("x"));

        var input = List.of("x", "illegal", "x");

        boolean result = sut.isValid(input, context);

        assertThat(result).isFalse();
        verifyCustomConstraintViolation("illegal value, allowed values are: [x]", listElement(1));
    }

    @Test
    void isValid_onlyAllowedValuesInList_noViolation() {
        when(allowedValuesProvider.getAllowedValues()).thenReturn(Set.of("x"));

        var input = List.of("x");

        boolean result = sut.isValid(input, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_null_noViolation() {
        boolean result = sut.isValid(null, context);

        assertThat(result).isTrue();
    }
}
