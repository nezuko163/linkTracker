package backend.academy.bot.domain.model;

import java.util.List;
import lombok.*;
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
