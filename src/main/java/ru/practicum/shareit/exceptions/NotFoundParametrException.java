package ru.practicum.shareit.exceptions;

public class NotFoundParametrException extends RuntimeException {
    public NotFoundParametrException(String message) {
        super(message);
    }

    public NotFoundParametrException(String message, Throwable cause) {
        super(message, cause);
    }
}
