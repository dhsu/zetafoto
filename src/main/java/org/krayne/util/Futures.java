package org.krayne.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Futures {
    private Futures() {}

    public static <T> BiConsumer<T, Throwable> handle(Consumer<T> onSuccess, Consumer<Throwable> onFailure) {
        return (result, t) -> {
            if (t == null) {
                onSuccess.accept(result);
            } else {
                onFailure.accept(t);
            }
        };
    }

    public static <T> CompletableFuture<T> exceptional(Throwable t) {
        CompletableFuture<T> f = new CompletableFuture<>();
        f.completeExceptionally(t);
        return f;
    }

}
