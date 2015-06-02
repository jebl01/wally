package jebl01.wally.utils;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Options;
import com.google.common.collect.Lists;

import java.util.List;

public class OptionUtils {
    public static <T> Option<List<T>> newOptionArrayList(Iterable<T> iterable) {
        final List<T> list = Lists.newArrayList(iterable);
        return list.isEmpty() ? Option.<List<T>>none() : Option.some(list);
    }
}
