package net.lshift.spki.convert;

import java.util.HashMap;
import java.util.Map;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.sexpform.Sexp;

public class ReadInfo {
    public static final ReadInfo BASE = new ReadInfo();

    private final Map<String, Class<?>> discrimMap;

    private ReadInfo() {
        discrimMap = new HashMap<String,Class<?>>();
    }

    private ReadInfo(ReadInfo base, Class<?>... classes) {
        discrimMap = new HashMap<String,Class<?>>(base.discrimMap);
        for (Class<?> clazz: classes) {
            register(discrimMap, clazz);
        }
    }

    public ReadInfo extend(Class<?>... classes) {
        return new ReadInfo(this, classes);
    }

    public <T> T read(
        final Class<T> clazz,
        final Map<Class<?>, Converter<?>> extraConverters,
        final Sexp sexp)
        throws InvalidInputException {
        return getConverter(extraConverters, clazz).read(this, sexp);
    }

    @SuppressWarnings("unchecked")
    public static <T> Converter<T> getConverter(
        final Map<Class<?>, Converter<?>> extraConverters,
        final Class<T> clazz) {
        if (extraConverters != null && extraConverters.containsKey(clazz)) {
            return (Converter<T>) extraConverters.get(clazz);
        } else {
            return ConverterCache.getConverter(clazz);
        }
    }

    public <T> T read(
        final Class<T> clazz,
        final Sexp sexp)
        throws InvalidInputException {
        return read(clazz, null, sexp);
    }

    private static <U> String discrimMapKey(final Class<U> clazz, final String name) {
        return clazz.getCanonicalName() + ";" + name;
    }

    @SuppressWarnings("unchecked")
    public <U> Class<? extends U> getExtraDiscrim(
        final Class<U> clazz,
        final String name) {
        // FIXME: class <-> canonicalName is not a 1-1 map.
        return (Class<? extends U>)
            discrimMap.get(discrimMapKey(clazz, name));
    }

    private static void register(Map<String, Class<?>> discrimMap, final Class<?> clazz) {
        Class<?> superclass = DiscriminatingConverter.getDiscriminatedSuperclass(clazz);
        final Converter<?> converter = ConverterCache.getConverter(clazz);
        if (!(converter instanceof ListConverter<?>)) {
            throw new ConvertReflectionException(clazz,
                "defines no name, cannot be instance of a discrim");
        }
        final String key = discrimMapKey(
            superclass, ((ListConverter<?>)converter).getName());
        if (discrimMap.containsKey(key)) {
            if (discrimMap.get(key) == clazz)
                return;
            throw new ConvertReflectionException(clazz,
                "cannot claim this key, " + discrimMap.get(key).getSimpleName() +
                " has it: " + key);
        }
        discrimMap.put(key, clazz);
    }
}
