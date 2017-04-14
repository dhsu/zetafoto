package org.krayne.datasource;


import java.util.Optional;

public class OpResult {
    public enum Type {
        DELETED,
        UPDATED,
        CREATED,
        NON_EXISTENT,
        ALREADY_EXISTING,
        STATE_MISMATCH
    }

    private final Type type;
    private final Optional<String> message;

    public static OpResult deleted() {
        return new OpResult(Type.DELETED, Optional.empty());
    }

    public static OpResult updated() {
        return new OpResult(Type.UPDATED, Optional.empty());
    }

    public static OpResult created() {
        return new OpResult(Type.CREATED, Optional.empty());
    }

    public static OpResult nonExistent() {
        return new OpResult(Type.NON_EXISTENT, Optional.empty());
    }

    public static OpResult alreadyExisting() {
        return new OpResult(Type.ALREADY_EXISTING, Optional.empty());
    }

    public static OpResult stateMismatch(String message) {
        return new OpResult(Type.STATE_MISMATCH, Optional.of(message));
    }

    public OpResult(Type type, Optional<String> message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return this.type;
    }

    public Optional<String> getMessage() {
        return this.message;
    }
}
