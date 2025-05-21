package backend.academy.scrapper.unit;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class MyTest {
    private static final Logger log = LogManager.getLogger(MyTest.class);
    private final ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

    @Test
    public void test() {
        map.put(1, 1);
        var s = 1;
        var a = map.compute(s, (key, value) -> {
            if (value == null) {
                return null;
            }
            if (value == 1) {
                map.remove(s);
            }
            return value - 1;
        });
        log.info("map - {}", map);
        log.info("a - {}", a);
    }
}
