package com.ebendal;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@ValidPropertyDependingOnSibling
@ValidListElementsDependingOnSibling
public class ValidatedObject {

    @ValidListElementsAllowedValuesRuntimeRetrieved
    private final List<String> listElements;

    @ValidPropertyAllowedValuesRuntimeRetrieved
    private final String property;

    private final boolean illegalListElementsAllowed;

}
