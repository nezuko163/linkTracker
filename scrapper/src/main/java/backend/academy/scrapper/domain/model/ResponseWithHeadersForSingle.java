package backend.academy.scrapper.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class ResponseWithHeadersForSingle<T> extends ResponseWithHeaders {
    private T body;

    public ResponseWithHeadersForSingle(T body, HttpStatusCode status, HttpHeaders headers) {
        super(status, headers);
        this.body = body;
    }
}
