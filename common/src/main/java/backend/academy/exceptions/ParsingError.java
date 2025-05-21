package backend.academy.exceptions;

public class ParsingError extends RuntimeException {
    public ParsingError(String message, String url) {
        super("Ошибка при парсинге ссылки: " + url + "\n" + message);
    }
}
