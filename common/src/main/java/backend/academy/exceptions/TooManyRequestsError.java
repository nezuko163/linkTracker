package backend.academy.exceptions;

public class TooManyRequestsError extends RuntimeException {
    public TooManyRequestsError(String ip) {
        super("Превышен лимит запросов с адреса: " + ip);
    }
}
