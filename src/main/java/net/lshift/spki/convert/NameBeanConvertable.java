package net.lshift.spki.convert;

/**
 * Superclass for objects that should be converted using the NameBean protocol
 */
public abstract class NameBeanConvertable implements Convertable
{
    public static <T> Converter<T> getConverter(Class<T> clazz) {
        return new NameBeanConverter<T>(clazz);
    }
}
