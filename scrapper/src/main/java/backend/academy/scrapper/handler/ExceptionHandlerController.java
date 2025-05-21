package backend.academy.scrapper.handler;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.exceptions.NotFoundError;
import backend.academy.exceptions.ParsingError;
import backend.academy.exceptions.TooManyRequestsError;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleIncorrectRequest("Неверные параметры запроса", ex, HttpStatusCode.valueOf(400));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleIncorrectRequest("Нечитаемый запрос", ex, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler({NotFoundError.class, ParsingError.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundError ex) {
        return handleIncorrectRequest(ex.getMessage(), ex, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMissingRequestHeaderException(Exception ex) {
        return handleIncorrectRequest(ex.getMessage(), ex, HttpStatusCode.valueOf(400));
    }

    @ExceptionHandler(TooManyRequestsError.class)
    public ResponseEntity<Object> handleTooManyRequestsException(TooManyRequestsError ex) {
        return handleIncorrectRequest(ex.getMessage(), ex, HttpStatusCode.valueOf(429));
    }

    private ResponseEntity<Object> handleIncorrectRequest(String description, Exception ex, HttpStatusCode status) {
        log.error("Ошибка - {}, {}", description, ex.getMessage());
        return new ResponseEntity<>(
                new ApiErrorResponse(
                        description,
                        String.valueOf(status.value()),
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        Arrays.stream(ex.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()),
                status);
    }
}
