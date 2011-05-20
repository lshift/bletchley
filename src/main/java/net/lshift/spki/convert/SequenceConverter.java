package net.lshift.spki.convert;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lshift.spki.Create;
import net.lshift.spki.Sexp;
import net.lshift.spki.Slist;

/**
 * Converter for a class that has a single field of type List.
 */
public class SequenceConverter<T> extends BeanConverter<T>
{
    private final String beanName;
    private final Class<?> contentType;

    public SequenceConverter(Class<T> clazz)
    {
        super(clazz);
        Annotation[][] annotations = constructor.getParameterAnnotations();
        if (annotations.length != 1) {
            throw new ConvertException(
                "Constructor must be one argument:"
                + clazz.getCanonicalName());
        }
        beanName = getPAnnotation(annotations[0]);
        Type[] pTypes = constructor.getGenericParameterTypes();
        if (!(pTypes[0] instanceof ParameterizedType)) {
            throw new ConvertException(
                "Constructor argument must be parameterized List type:"
                + clazz.getCanonicalName());
        }
        ParameterizedType pType = (ParameterizedType) pTypes[0];
        if (!List.class.equals(pType.getRawType())) {
            throw new ConvertException(
                "Constructor argument must be List type:"
                + clazz.getCanonicalName());
        }
        Type[] typeArgs = pType.getActualTypeArguments();
        if (typeArgs.length != 1) {
            throw new ConvertException(
                "Constructor type must have one parameter"
                + clazz.getCanonicalName());
        }
        contentType = (Class<?>) typeArgs[0];
    }

    @Override
    public T fromSexp(Sexp sexp)
    {
        Slist slist = (Slist) sexp;
        if (!Create.atom(name).equals(slist.getHead())) {
            throw new ConvertException("Expected " + name +
                " but was " + slist.getHead());
        }
        List<Sexp> tail = slist.getSparts();
        List<Object> components = new ArrayList<Object>(tail.size());
        for (Sexp tailPart: tail) {
            components.add(Convert.fromSExp(contentType, tailPart));
        }
        Object[] initargs = new Object[1];
        initargs[0] = Collections.unmodifiableList(components);
        try {
            return constructor.newInstance(initargs);
        } catch (InstantiationException e) {
            throw new ConvertReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ConvertReflectionException(e);
        } catch (InvocationTargetException e) {
            throw new ConvertReflectionException(e);
        }
    }

    @Override
    public Sexp toSexp(T o)
    {
        try {
            List<?> property = (List<?>)  clazz.getField(beanName).get(o);
            List<Sexp> components = new ArrayList<Sexp>(property.size());
            for (Object v: property) {
                components.add(Convert.toSExpUnchecked(contentType, v));
            }
            return Create.list(name, components);
        } catch (IllegalAccessException e) {
            throw new ConvertReflectionException(e);
        } catch (NoSuchFieldException e) {
            throw new ConvertReflectionException(e);
        }
    }

    @Override
    public void write(ConvertOutputStream out, T o)
        throws IOException
    {
        try {
            out.beginSexp();
            writeName(out);
            List<?> property = (List<?>) clazz.getField(beanName).get(o);
            for (Object v: property) {
                out.writeUnchecked(contentType, v);
            }
        out.endSexp();
        } catch (IllegalAccessException e) {
            throw new ConvertReflectionException(e);
        } catch (NoSuchFieldException e) {
            throw new ConvertReflectionException(e);
        }
    }

}
