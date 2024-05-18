package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class HandlerException {
    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundError(final RuntimeException e) {
        log.debug("Get error 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse("Not found", e.getMessage());
    }

    @ExceptionHandler({EmailDuplicateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse duplicate(final RuntimeException e) {
        log.debug("Get error 409 Conflict {}", e.getMessage(), e);
        return new ErrorResponse("Duplicates was found", e.getMessage());
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MethodArgumentNotValidException.class,
            AvailableException.class, CommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(final Exception e) {
        log.debug("Get error 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse("Bad Request", e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class,
            UnsupportedStatusException.class})
    @ResponseStatus(reason = "Unknown state: UNSUPPORTED_STATUS", code=HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse statusError(final Throwable e) {
        log.debug("Get status 500 Internal Server Error {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerError(final Throwable e) {
        log.debug("Get error 500 Internal Server Error {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }
}