package brachy84.brachydium.api.util;

import org.jetbrains.annotations.NotNull;

public class ValidationResult<T> {

    private final State state;
    private final T result;

    public ValidationResult(State state, T result) {
        this.state = state;
        this.result = result;
    }

    public static <T> ValidationResult<T> valid(T result) {
        return new ValidationResult<T>(State.VALID, result);
    }

    public static <T> ValidationResult<T> invalid(T result) {
        return new ValidationResult<T>(State.INVALID, result);
    }

    public static <T> ValidationResult<T> skip(T result) {
        return new ValidationResult<T>(State.SKIP, result);
    }

    public ValidationResult<T> withState(State state) {
        return new ValidationResult<>(state, result);
    }

    public boolean isValid() {
        return state == State.VALID;
    }

    public boolean isInvalid() {
        return state == State.INVALID;
    }

    public boolean doSkip() {
        return state == State.SKIP;
    }

    public State getState() {
        return state;
    }

    public T getResult() {
        return result;
    }

    public enum State {
        VALID,
        INVALID,
        SKIP
    }
}
