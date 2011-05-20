package net.lshift.spki.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import net.lshift.spki.Create;
import net.lshift.spki.Sexp;
import net.lshift.spki.Slist;

/**
 * Superclass for a converter that reads bean properties based on
 * an annotated constructor.
 */
public abstract class BeanFieldConverter<T> extends BeanConverter<T>
{
    private final FieldConvertInfo[] fields;

    public BeanFieldConverter(Class<T> clazz)
    {
        super(clazz);
        Class<?>[] parameters = constructor.getParameterTypes();
        Annotation[][] annotations = constructor.getParameterAnnotations();
        assert parameters.length == annotations.length;
        fields = new FieldConvertInfo[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            fields[i] = new FieldConvertInfo(
                getPAnnotation(annotations[i]), parameters[i]);
        }
    }

    @Override
    public Sexp toSexp(T o)
    {
        try {
            Sexp[] components = new Sexp[fields.length];
            for (int i = 0; i < fields.length; i++) {
                final Object property =
                    clazz.getField(fields[i].getName()).get(o);
                components[i] = fieldToSexp(fields[i],
                    Convert.toSExpUnchecked(fields[i].getType(), property));
            }
            return Create.list(name, components);
        } catch (IllegalAccessException e) {
            throw new ConvertReflectionException(e);
        } catch (NoSuchFieldException e) {
            throw new ConvertReflectionException(e);
        }
    }

    protected abstract Sexp fieldToSexp(
        FieldConvertInfo fieldConvertInfo,
        Sexp sexp);

    @Override
    public T fromSexp(Sexp sexp)
    {
        Slist slist = (Slist) sexp;
        if (!Create.atom(name).equals(slist.getHead())) {
            throw new ConvertException("Expected " + name +
                " but was " + slist.getHead());
        }
        Object[] initargs = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            initargs[i] = Convert.fromSExp(fields[i].getType(),
                getSExp(fields[i], i, slist));
        }
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

    protected abstract Sexp getSExp(
        FieldConvertInfo fieldConvertInfo,
        int i,
        Slist slist);
}
