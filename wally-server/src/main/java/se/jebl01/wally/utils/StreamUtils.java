package se.jebl01.wally.utils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class StreamUtils {
  public static <T, A extends List<R>, R> Stream<R> leftListFold(Stream<T> input, Supplier<A> accumulator, BiConsumer<A, T> consumer) {
    Collector<T, A, Stream<R>> collector = Collector.of(
        accumulator,
        consumer,
        (acc1, acc2) -> {
          A newAccu = accumulator.get();
          newAccu.addAll(acc1);
          newAccu.addAll(acc2);
          return newAccu;
        },
        acc -> acc.stream());
    
    return input.collect(collector);
  }
}
