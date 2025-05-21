package backend.academy.bot.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StateMachine {
    private State state = State.NONE;
    private String url;
    private String[] tags;
    private String[] filters;
    private String digestTime;

    public void setEvent(Event event) {
        switch (event) {
            case START_TRACK -> state = State.WAITING_LINK;
            case ENTER_LINK -> state = State.WAITING_FILTERS;
            case ENTER_FILTER -> state = State.WAITING_TAGS;
            case START_UNTRACKING_LINK -> state = State.WAITING_UNTRACK_LINK;
            case ENTER_TAG -> state = State.FINISHED;
            case CLOSE -> {
                state = State.NONE;
                url = null;
                tags = null;
                filters = null;
            }
        }
    }

    public enum State {
        NONE,
        WAITING_LINK,
        WAITING_TAGS,
        WAITING_FILTERS,
        WAITING_UNTRACK_LINK,
        WAITING_DIGEST_TIME,
        FINISHED
    }

    public enum Event {
        START_TRACK,
        ENTER_LINK,
        ENTER_TAG,
        ENTER_FILTER,
        START_DIGEST_TIME,
        START_UNTRACKING_LINK,
        CLOSE
    }
}
