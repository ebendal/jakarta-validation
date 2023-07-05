package com.ebendal;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AllowedValuesProvider {

    public Set<String> getAllowedValues() {
        return Set.of("one", "two");
    }
}
