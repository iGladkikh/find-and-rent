package ru.practicum.shareit.common.exception;

public class DuplicatedDataException extends RuntimeException {

    public DuplicatedDataException(String message) {
        super(message);
    }
}