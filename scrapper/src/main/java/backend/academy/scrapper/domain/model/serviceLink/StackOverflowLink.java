package backend.academy.scrapper.domain.model.serviceLink;

import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.ObservableService;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
@RequiredArgsConstructor
public class StackOverflowLink extends CustomLink {
    private final Long questionId;

    public StackOverflowLink() {
        questionId = null;
    }

    public StackOverflowLink(String url, Long questionId) {
        super(url);
        this.questionId = questionId;
    }

    @Override
    public ObservableService service() {
        return ObservableService.STACK_OVERFLOW;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(questionId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (questionId == null) return false;
        if (obj instanceof StackOverflowLink link) {
            return questionId.equals(link.questionId);
        }
        return false;
    }
}
