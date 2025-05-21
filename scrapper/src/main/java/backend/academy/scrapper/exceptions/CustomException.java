package backend.academy.scrapper.exceptions;

import lombok.Getter;

public class CustomException extends RuntimeException {
    @Getter
    private final Integer code;

    public CustomException(String message, Integer code) {
        super(message);
        this.code = code;
    }
}
