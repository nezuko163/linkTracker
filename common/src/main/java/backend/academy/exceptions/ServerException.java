package backend.academy.exceptions;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final int code;

    public ServerException(int code, String message) {
        super("code - " + code + "\n" + message);
        this.code = code;
    }
}
