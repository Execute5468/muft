package net.adequoir.muft.util;

import java.lang.reflect.Array;
import java.util.function.Function;

public final class ArrayUtil {
    @SuppressWarnings("unchecked")
    public static <T> T[] create(Class<T> klass, int length) {
        return (T[]) Array.newInstance(klass, length);
    }

    public static <T, U> U[] mapTo(T[] src, Function<? super T, ? extends U> mapper, Class<U> klass) {
        U[] dest = create(klass, src.length);
        for (int i = 0; i < src.length; i++) dest[i] = mapper.apply(src[i]);
        return dest;
    }

    private ArrayUtil() {}
}
