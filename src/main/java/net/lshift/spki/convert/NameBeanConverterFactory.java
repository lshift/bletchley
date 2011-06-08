package net.lshift.spki.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.lshift.spki.convert.Convert.ByName;

public class NameBeanConverterFactory
implements ConverterFactory
{
    public <T> Converter<T> converter(final Class<T> clazz, final Annotation a) {
        final ByName aa = (Convert.ByName)a;
        final List<FieldConvertInfo> fields = getFieldMap(clazz);
        return new NameBeanConverter<T>(clazz, aa.value(), fields);
    }

    private <T> List<FieldConvertInfo> getFieldMap(final Class<T> clazz) {
        final List<FieldConvertInfo> fields = new ArrayList<FieldConvertInfo>();
        addFields(clazz, fields);
        return fields;
    }

    private <T> void addFields(final Class<T> clazz, final List<FieldConvertInfo> fields) {
        final Class<? super T> sup = clazz.getSuperclass();
        if (sup != null) {
            addFields(sup, fields);
        }
        for (final Field f: clazz.getDeclaredFields()) {
            fields.add(new FieldConvertInfo(f));
        }
    }
}
