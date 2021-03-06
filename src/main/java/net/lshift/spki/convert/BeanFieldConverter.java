package net.lshift.spki.convert;

import java.util.List;
import net.lshift.spki.sexpform.Sexp;

/**
 * Superclass for a converter that reads bean properties based on
 * an annotated constructor.
 */
public abstract class BeanFieldConverter<T>
    extends BeanConverter<T> {

    protected final List<FieldConvertInfo> fields;

    public BeanFieldConverter(final Class<T> clazz, final String name, final List<FieldConvertInfo> fields)
    {
        super(clazz, name);
        this.fields = fields;
    }

    @Override
    public void writeRest(final T o, final List<Sexp> out) {
        try {
            for (final FieldConvertInfo f: fields) {
                final Object property =
                    f.field.get(o);
                final Sexp sexp = writeField(f, property);
                if (sexp != null)
                    out.add(sexp);
            }
        } catch (final IllegalAccessException e) {
            throw new ConvertReflectionException(this, clazz, e);
        }
    }

    protected abstract Sexp writeField(
        FieldConvertInfo fieldConvertInfo,
        Object property);
}
