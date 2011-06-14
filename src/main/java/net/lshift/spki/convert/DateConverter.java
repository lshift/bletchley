package net.lshift.spki.convert;

import java.util.Date;

import net.lshift.spki.Constants;

/**
 * Convert between a Date and a SExp
 */
public class DateConverter
    extends StepConverter<Date, String> {
    @Override
    public Class<Date> getResultClass() { return Date.class; }

    @Override
    protected Class<String> getStepClass() { return String.class; }

    @Override
    protected String stepIn(final Date o) {
        return Constants.DATE_FORMAT.format(o);
    }

    @Override
    protected Date stepOut(final String o) {
        try {
            return Constants.DATE_FORMAT.parse(o);
        } catch (final java.text.ParseException e) {
            throw new ConvertException(e);
        }
    }
}
