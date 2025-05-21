package backend.academy.exceptions;

public class NotFoundError extends RuntimeException {
    public NotFoundError(String message) {
        super(message);
    }
}
