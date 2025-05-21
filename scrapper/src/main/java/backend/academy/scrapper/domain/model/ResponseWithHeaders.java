package backend.academy.scrapper.domain.model;

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
public class ResponseWithHeaders {
    private HttpStatusCode status;
    private HttpHeaders headers;
}
