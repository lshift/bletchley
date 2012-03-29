package net.lshift.spki.convert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.sexpform.Sexp;

/**
 * Converter for a class that has a single field of type List.
 */
public class SequenceConverter<T>
    extends BeanConverter<T> {
    private final Field beanField;
    private final Class<?> contentType;

    public SequenceConverter(final Class<T> clazz, final String name, final Field field) {
        super(clazz, name);
        beanField = field;
        contentType = getTypeInList(clazz, field);
    }

    public static <T> Class<?> getTypeInList(final Class<T> clazz, final Field field) {
        if (!(field.getGenericType() instanceof ParameterizedType)) {
            throw new ConvertReflectionException(clazz,
                "Field must be parameterized List type");
        }
        final ParameterizedType pType = (ParameterizedType) field.getGenericType();
        if (!List.class.equals(pType.getRawType())) {
            throw new ConvertReflectionException(clazz,
                "Constructor argument must be List type");
        }
        final Type[] typeArgs = pType.getActualTypeArguments();
        if (typeArgs.length != 1) {
            throw new ConvertReflectionException(clazz,
                "Constructor type must have one parameter");
        }
        return (Class<?>) typeArgs[0];
    }

    @Override
    public void writeRest(final Converting c, final T o, final List<Sexp> tail) {
        try {
            final List<?> property = (List<?>) beanField.get(o);
            for (final Object v: property) {
                tail.add(c.writeUnchecked(contentType, v));
            }
        } catch (final IllegalAccessException e) {
            throw new ConvertReflectionException(clazz, e);
        }
    }

    @Override
    protected Map<Field, Object> readFields(final Converting c, final List<Sexp> tail)
        throws InvalidInputException {
            final Map<Field, Object> fields = new HashMap<Field, Object>();
            fields.put(beanField, readSequence(c, contentType, tail));
            return fields;
    }

    public static List<Object> readSequence(
        final Converting c,
        final Class<?> contentType,
        final List<Sexp> in) throws InvalidInputException {
        final List<Object> components = new ArrayList<Object>(in.size());
        for (final Sexp s: in) {
            components.add(c.read(contentType, s));
        }
        return Collections.unmodifiableList(components);
    }
}
