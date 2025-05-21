package backend.academy.model;

public record TagModel(long id, String value) {
    @Override
    public String toString() {
        return value;
    }
}
