package TechStore.app.util;

import java.util.function.Consumer;

public final class FunctionInterface {
    @FunctionalInterface
    public interface Function5<A, B, C, D, E, R> {
        public R apply(A one, B two, C three, D four, E five);
    }

    @FunctionalInterface
    public interface TripTransaction<A, B, C, D, R> {
        public R apply(A one, B two, C three, D four);
    }

    @FunctionalInterface
    public interface Function3<A, B, C, R> {
        public R apply(A one, B two, C three);
    }

    @FunctionalInterface
    public interface Function2<A, B, R> {
        public R apply(A one, B two);
    }


    public static <T> void setIfNotNull(final Consumer<T> setter, final T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
