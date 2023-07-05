package com.ebendal;

import com.ebendal.jakarta.validation.CustomConstraintValidatorTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ValidPropertyAllowedValuesRuntimeRetrievedValidatorTest extends CustomConstraintValidatorTest {

    @Mock
    private AllowedValuesProvider allowedValuesProvider;

    @InjectMocks
    private ValidPropertyAllowedValuesRuntimeRetrieved.Validator sut;

    @Test
    void isValid_valueNotAllowed_violation() {
        when(allowedValuesProvider.getAllowedValues()).thenReturn(Set.of("x"));

        var input = "something-else";

        boolean result = sut.isValid(input, context);

        assertThat(result).isFalse();
        verifyCustomConstraintViolation("illegal value, allowed values are: [x]");
    }

    @Test
    void isValid_valueAllowed_noViolation() {
        when(allowedValuesProvider.getAllowedValues()).thenReturn(Set.of("x"));

        var input = "x";

        boolean result = sut.isValid(input, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_null_noViolation() {
        boolean result = sut.isValid(null, context);

        assertThat(result).isTrue();
    }
}
