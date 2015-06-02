package jebl01.wally.utils;

import com.atlassian.fugue.Option;
import com.google.common.base.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {

    private static Map<Class<?>, Method> JSON_TYPES = new HashMap<>();

    static {
        try {
            JSON_TYPES.put(String.class, JSONObject.class.getMethod("getString", String.class));
            JSON_TYPES.put(Integer.class, JSONObject.class.getMethod("getInt", String.class));
            JSON_TYPES.put(Long.class, JSONObject.class.getMethod("getLong", String.class));
            JSON_TYPES.put(Double.class, JSONObject.class.getMethod("getDouble", String.class));
            JSON_TYPES.put(Boolean.class, JSONObject.class.getMethod("getBoolean", String.class));
            JSON_TYPES.put(JSONArray.class, JSONObject.class.getMethod("getJSONArray", String.class));
            JSON_TYPES.put(JSONObject.class, JSONObject.class.getMethod("getJSONObject", String.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> Option<T> getValue(final JSONObject jsonObject, final String label, Class<T> type) {

        return Option.option(JSON_TYPES.get(type)).flatMap(new Function<Method, Option<T>>() {
            public Option<T> apply(Method method) {
                try {
                    return Option.option((T)method.invoke(jsonObject, label));
                } catch (Exception e) {
                    //TODO: message?
                    return Option.none();
                }
            }
        });
    }

    public static <T> Iterable<T> asIterable(final Iterator<T> iterator) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }
}
