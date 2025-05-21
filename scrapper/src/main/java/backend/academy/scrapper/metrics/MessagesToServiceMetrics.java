package backend.academy.scrapper.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MessagesToServiceMetrics {

    private final Counter messageCounter;

    public MessagesToServiceMetrics(MeterRegistry registry) {
        this.messageCounter = registry.counter("user_messages_total");
    }

    public void incrementMessageCount() {
        messageCounter.increment();
    }
}
