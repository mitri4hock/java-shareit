package ru.practicum.shareit.exceptions;

public class BadParametrException extends RuntimeException{
    public BadParametrException(String message) {
        super(message);
    }

    public BadParametrException(String message, Throwable cause) {
        super(message, cause);
    }
}
