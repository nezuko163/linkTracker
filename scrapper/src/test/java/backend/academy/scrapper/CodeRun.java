package backend.academy.scrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class CodeRun {
    private static final Logger log = LogManager.getLogger(CodeRun.class);
    private ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @SneakyThrows
    public void main(String[] args) {
        Mono.fromCallable(() -> {
                    log.info("start {}", Thread.currentThread().getName());
                    Thread.sleep(1000);
                    log.info("end");
                    return 2L;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(res -> {
                    log.info("start sub {}", Thread.currentThread().getName());
                    asd(res).subscribe();
                    log.info("end sub {}", Thread.currentThread().getName());
                    return 2L;
                })
                .map(res -> {
                    log.info("start sub2 {}", Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("end sub2 {}", Thread.currentThread().getName());
                    return res;
                })
                .subscribe(res -> log.info("результат {}", res));
        log.info("main");
        Thread.sleep(10000L);
    }

    @SneakyThrows
    public Mono<Boolean> asd(Long a) {
        return Mono.fromCallable(() -> {
                    log.info("asd: {}", Thread.currentThread().getName());
                    Thread.sleep(3000L);
                    log.info("asd: {}", Thread.currentThread().getName());
                    return true;
                })
                .publishOn(Schedulers.fromExecutorService(executor));
    }
}
