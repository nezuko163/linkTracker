package backend.academy.scrapper.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWithHeadersForArray<T> extends ResponseWithHeaders {
    private List<T> body;

    public ResponseWithHeadersForArray(List<T> body, HttpStatusCode status, HttpHeaders headers) {
        super(status, headers);
        this.body = body;
    }
}
