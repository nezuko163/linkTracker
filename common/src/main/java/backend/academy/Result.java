package backend.academy;

public record Result<T>(Status status, T data, String message) {
    public enum Status {
        SUCCESS,
        FAILURE,
        LOADING,
        NONE
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(Status.SUCCESS, data, null);
    }

    public static <T> Result<T> failure(String message, T data) {
        return new Result<T>(Status.FAILURE, data, message);
    }

    public static <T> Result<T> failure(String message) {
        return failure(message, null);
    }

    public static <T> Result<T> loading() {
        return new Result<T>(Status.LOADING, null, null);
    }

    public static <T> Result<T> none() {
        return new Result<T>(Status.NONE, null, null);
    }
}
