package com.lewisd.authrite.acctests.framework;

import java.util.function.Supplier;

public class Lazily {
    private Lazily() {
        throw new UnsupportedOperationException();
    }

    /**
     * Lazy-init helper. Returns the first argument if non-null, or invokes the second argument's {@link
     * Supplier#get()} method and returns the result otherwise. <p> Intended usage pattern: </p>
     * <pre>
     *     public Config config() {
     *         return this.config = Lazily.create(this.config, this::loadConfigFromFile);
     *     }
     * </pre>
     * <p> You can also pass a lambda or a pre-existing {@link Supplier} instead of a method handle.
     * </p>
     *
     * @param value
     *         the initializable field's current value.
     * @param initialiser
     *         a factory for creating a new value.
     * @param <T>
     *         the type of the objects being lazily created.
     * @return either <var>value</var> or a new object from <var>initialiser</var>.
     */
    public static <T> T create(T value, Supplier<? extends T> initialiser) {
        if (value == null)
            return initialiser.get();
        return value;
    }
}
