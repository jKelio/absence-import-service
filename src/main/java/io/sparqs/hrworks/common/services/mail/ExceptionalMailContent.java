package io.sparqs.hrworks.common.services.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Builder
@Getter
class ExceptionalMailContent {
    private final String customMessage;

    @JsonIgnore
    private Throwable exception;

    public String getMessage() {
        return exception.getMessage();
    }

    public Collection<String> getStackTraces() {
        return Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
    }
}
