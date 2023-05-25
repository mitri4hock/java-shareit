package ru.practicum.shareit.exceptions;

public class ConflictParametrException extends RuntimeException {
    public ConflictParametrException(String message) {
        super(message);
    }

    public ConflictParametrException(String message, Throwable cause) {
        super(message, cause);
    }
}
