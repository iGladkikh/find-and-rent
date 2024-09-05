package ru.practicum.shareit.common;

public class LoggerMessagePattern {
    public static final String DEBUG = "Action: {}, data: {}";
    public static final String ERROR = "Action: {}, data: {}, message: {}, exception: {}";

    private LoggerMessagePattern() {
    }
}
