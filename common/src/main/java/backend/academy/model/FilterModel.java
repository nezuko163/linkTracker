package backend.academy.model;

public record FilterModel(long id, String filter, String value) {
    @Override
    public String toString() {
        return filter + ": " + value;
    }

    public static FilterModel toFilter(String filter) {
        var indexSeparation = filter.indexOf(": ");
        if (indexSeparation == -1) {
            return null;
        }
        return new FilterModel(-1L, filter.substring(0, indexSeparation), filter.substring(indexSeparation + 2));
    }
}
