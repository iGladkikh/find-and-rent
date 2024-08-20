package ru.practicum.shareit.common.exception.handler;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.common.exception.DataNotAvailableException;
import ru.practicum.shareit.common.exception.DataNotFoundException;
import ru.practicum.shareit.common.exception.DuplicatedDataException;
import ru.practicum.shareit.common.exception.ForbiddenException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({BindException.class,
            ConstraintViolationException.class,
            ValidationException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            DataNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({DataNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({DuplicatedDataException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedData(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(Exception e) {
        return new ErrorResponse(e.getMessage());
    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDefault() {
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
