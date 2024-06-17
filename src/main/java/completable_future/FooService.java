package completable_future;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 문제의 코드 똑 떼왔음
 */
@Slf4j
class FooService {

    public int processResult(List<CompletableFuture<? extends Object>> futures, int timeout) {
        exceptionally(futures);
        return getSendResult(futures, timeout);
    }

    void exceptionally(List<CompletableFuture<? extends Object>> futures) {
        futures.forEach(future -> future.exceptionally(ex -> {
            log.error("Failed to send message !@##@!#%Q%", ex); // 에러 로그를 찍는다.
            throw new IllegalStateException("Failed to send message to Kafka", ex); // 여기서 던진 exception은 안나옴
        }));
    }

    int getSendResult(List<CompletableFuture<? extends Object>> rs, int timeout) {
        AtomicInteger count = new AtomicInteger();
        rs.forEach(r -> {
            try {
                String result = (String) r.get(timeout, TimeUnit.SECONDS);
                count.getAndIncrement();
                log.info("Send result: {}", result);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
        return count.intValue();
    }
}
