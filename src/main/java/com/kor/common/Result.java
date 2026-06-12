package com.kor.common;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T, E> {
    private final T value;
    private final E error;
    private final boolean isOk;

    private Result(T value, E error, boolean isOk) {
        this.value = value;
        this.error = error;
        this.isOk = isOk;
    }

    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(value, null, true);
    }

    public static <T, E> Result<T, E> err(E error) {
        return new Result<>(null, error, false);
    }

    public boolean isOk() {
        return isOk;
    }

    public boolean isErr() {
        return !isOk;
    }

    public T unwrap() {
        if (isOk) return value;
        throw new RuntimeException("Called unwrap on Err value: " + error);
    }

    public E unwrapErr() {
        if (!isOk) return error;
        throw new RuntimeException("Called unwrapErr on Ok value: " + value);
    }

    public T expect(String message) {
        if (isOk) return value;
        throw new RuntimeException(message + ": " + error);
    }

    public Optional<T> ok() {
        return isOk ? Optional.of(value) : Optional.empty();
    }

    public Optional<E> err() {
        return isOk ? Optional.empty() : Optional.of(error);
    }

    public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
        if (isOk) return Result.ok(mapper.apply(value));
        return Result.err(error);
    }

    public <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
        if (isOk) return mapper.apply(value);
        return Result.err(error);
    }

    public <F> Result<T, F> mapErr(Function<? super E, ? extends F> mapper) {
        if (isOk) return Result.ok(value);
        return Result.err(mapper.apply(error));
    }

    public void onSuccess(Consumer<? super T> consumer) {
        if (isOk) consumer.accept(value);
    }

    public void onFailure(Consumer<? super E> consumer) {
        if (!isOk) consumer.accept(error);
    }

    public T orElse(T defaultValue) {
        return isOk ? value : defaultValue;
    }

    public T orElseGet(Function<? super E, ? extends T> supplier) {
        return isOk ? value : supplier.apply(error);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Result)) return false;
        Result<?, ?> other = (Result<?, ?>) obj;
        if (isOk != other.isOk) return false;
        if (isOk) return value.equals(other.value);
        return error.equals(other.error);
    }

    @Override
    public int hashCode() {
        return isOk ? (31 + value.hashCode()) : (17 + error.hashCode());
    }

    @Override
    public String toString() {
        if (isOk) return "Ok(" + value + ")";
        return "Err(" + error + ")";
    }
}