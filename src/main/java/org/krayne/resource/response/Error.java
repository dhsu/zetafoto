package org.krayne.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Error {
    @JsonProperty private final ImmutableList<String> messages;

    public Error(String message) {
        this.messages = ImmutableList.of(message);
    }

    public Error(List<String> messages) {
        this.messages = ImmutableList.copyOf(messages);
    }

    public ImmutableList<String> getMessages() {
        return this.messages;
    }
}