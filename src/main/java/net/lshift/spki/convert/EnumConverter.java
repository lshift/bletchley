package net.lshift.spki.convert;

import java.util.HashMap;
import java.util.Map;

import net.lshift.spki.InvalidInputException;

public class EnumConverter<T extends Enum<T>>
extends StringStepConverter<T> {
    private final Map<T, String> forwardMap = new HashMap<>();
    private final Map<String, T> backMap = new HashMap<>();

    public EnumConverter(final Class<T> resultClass) {
        super(resultClass);
        if (!resultClass.isEnum()) {
            throw new IllegalArgumentException();
        }
        for (final T t: resultClass.getEnumConstants()) {
            final String name = t.name();
            if (!ConvertUtils.isAsciiIdentifier(name)) {
                throw new IllegalArgumentException("Enum name is non ascii: " + clazz.getName() + "." + name);
            }
            final String conversion = name.toLowerCase().replace('_', '-');
            forwardMap.put(t, conversion);
            backMap.put(conversion, t);
        }
    }

    @Override
    protected T stepOut(final String s)
        throws InvalidInputException {
        final T res = backMap.get(s);
        if (res == null) {
            throw new ConvertException("not present in enum: " +s);
        }
        return res;
    }

    @Override
    protected String stepIn(final T o) {
        return forwardMap.get(o);
    }
}
