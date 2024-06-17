package completable_future;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FooServiceTest {

    @Test
    void 정상_케이스() {
        var futures = List.of(
            CompletableFuture.completedFuture("1"),
            CompletableFuture.completedFuture("2"),
            CompletableFuture.completedFuture(List.of())
        );

        var sut = new FooService();

        assertThat(sut.processResult(futures, 1)).isEqualTo(3);
    }

    @Test
    void exceptionally를_타서_에러로그를_찍게하자() {
        var futures = List.of(
            CompletableFuture.completedFuture("1"),
            CompletableFuture.failedFuture(new RuntimeException("에러 발생")),
            CompletableFuture.completedFuture("3")
        );

        var sut = new FooService();

        assertThatThrownBy(() -> sut.processResult(futures, 1))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("java.lang.RuntimeException: 에러 발생");
    }

    @Test
    void getSendResult_InterruptedException케이스() {
        // interruptedException이 발생하는 케이스 어떻게 재현하지?

        var future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "1";
        });

        var sut = new FooService();
        sut.processResult(List.of(future, future, future), 1);

        // assertThatThrownBy(() -> sut.processResult(List.of(future), 1))
        //     .isInstanceOf(RuntimeException.class)
        //     .hasMessage("java.lang.RuntimeException: java.lang.InterruptedException");


    }
}
