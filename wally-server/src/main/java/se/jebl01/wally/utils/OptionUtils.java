package se.jebl01.wally.utils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import com.atlassian.fugue.Option;

public class OptionUtils {
//  public static <T1, T2, R> Optional<R> lift2(Optional<T1> t1, Optional<T2> t2, BiFunction<T1, T2, R> f) {
//    return t1.flatMap(o -> t2.flatMap(o2 -> Optional.of(f.apply(o, o2))));
//  }
  
  public static <T1, T2, R> Stream<R> zip(Stream<T1> stream1, Stream<T2> stream2, BiFunction<T1, T2, R> f) {
    
    Objects.requireNonNull(f);
    Spliterator<T1> aSpliterator = (Spliterator<T1>) Objects.requireNonNull(stream1).spliterator();
    Spliterator<T2> bSpliterator = (Spliterator<T2>) Objects.requireNonNull(stream2).spliterator();

    // Zipping looses DISTINCT and SORTED characteristics
    int both = aSpliterator.characteristics() & bSpliterator.characteristics() &
            ~(Spliterator.DISTINCT | Spliterator.SORTED);
    int characteristics = both;

    long zipSize = ((characteristics & Spliterator.SIZED) != 0)
            ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
            : -1;

    Iterator<T1> aIterator = Spliterators.iterator(aSpliterator);
    Iterator<T2> bIterator = Spliterators.iterator(bSpliterator);
    Iterator<R> cIterator = new Iterator<R>() {
        @Override
        public boolean hasNext() {
            return aIterator.hasNext() && bIterator.hasNext();
        }

        @Override
        public R next() {
            return f.apply(aIterator.next(), bIterator.next());
        }
    };

    Spliterator<R> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
    
    return (stream1.isParallel() || stream2.isParallel())
           ? StreamSupport.stream(split, true)
           : StreamSupport.stream(split, false);
  }
  
  public static <T, R> Stream<R> flatMap(Option<T> t, Function<T, Stream<R>> f) {
    if(t.isEmpty()) return Stream.empty();
    return Objects.requireNonNull(f.apply(t.get()));
  }
  
  public static <T> Stream<T> streamOf(Option<T> value) {
    return value.isDefined() ? Stream.of(value.get()) : Stream.empty();
  }
}
