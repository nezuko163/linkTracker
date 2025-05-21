package backend.academy.bot.services;

import backend.academy.bot.domain.model.StateMachine;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class StateMachineService {
    private final ConcurrentHashMap<Long, StateMachine> stateMachineMap = new ConcurrentHashMap<>();

    public StateMachine setState(Long id, StateMachine stateMachine) {
        return stateMachineMap.put(id, stateMachine);
    }

    public void clear(Long id) {
        stateMachineMap.remove(id);
    }

    public StateMachine getStateMachine(Long id) {
        return stateMachineMap.get(id);
    }

    public StateMachine getAndCreate(Long id) {
        return stateMachineMap.computeIfAbsent(id, k -> new StateMachine());
    }

    public StateMachine setEvent(Long id, StateMachine.Event event) {
        return stateMachineMap.compute(id, (k, v) -> {
            if (v == null) v = new StateMachine();
            v.setEvent(event);
            return v;
        });
    }

    public StateMachine setDigestTime(Long id, String time) {
        return stateMachineMap.compute(id, (k, v) -> {
            if (v == null) v = new StateMachine();
            v.digestTime(time);
            v.setEvent(StateMachine.Event.START_DIGEST_TIME);
            return v;
        });
    }

    public StateMachine setUrl(Long id, String url) {
        return stateMachineMap.compute(id, (k, v) -> {
            if (v == null) v = new StateMachine();
            v.url(url);
            v.setEvent(StateMachine.Event.ENTER_LINK);
            return v;
        });
    }

    public StateMachine setTags(Long id, String[] tags) {
        return stateMachineMap.computeIfPresent(id, (k, v) -> {
            v.tags(tags);
            v.setEvent(StateMachine.Event.ENTER_TAG);
            return v;
        });
    }

    public StateMachine setFilters(Long id, String[] filters) {
        return stateMachineMap.computeIfPresent(id, (k, v) -> {
            v.filters(filters);
            v.setEvent(StateMachine.Event.ENTER_FILTER);
            return v;
        });
    }
}
