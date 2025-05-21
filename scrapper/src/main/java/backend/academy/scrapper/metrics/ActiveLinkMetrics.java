package backend.academy.scrapper.metrics;

import backend.academy.scrapper.domain.model.ObservableService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class ActiveLinkMetrics {
    private final ConcurrentHashMap<ObservableService, AtomicLong> activeLinks = new ConcurrentHashMap<>();

    public ActiveLinkMetrics(MeterRegistry registry) {
        registerType(registry, ObservableService.GITHUB);
        registerType(registry, ObservableService.STACK_OVERFLOW);
    }

    private void registerType(MeterRegistry registry, ObservableService type) {
        AtomicLong value = new AtomicLong(0);
        activeLinks.put(type, value);
        Gauge.builder("active_links", value, AtomicLong::get)
            .tag("type", type.name())
            .register(registry);
    }

    public void updateLinkCount(ObservableService type, long count) {
        activeLinks.get(type).set(count);
    }
}
