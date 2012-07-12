package net.lshift.spki.convert;

import net.lshift.spki.sexpform.Sexp;

public class SexpBacked
    implements Writeable {
    private Sexp sexp;

    @SuppressWarnings({ "cast", "unchecked" })
    @Override
    public synchronized Sexp toSexp() {
        if (sexp == null) {
            sexp = ((Converter<Object>) ConverterCache.getConverter(
                (Class<Object>)(Class<?>)getClass())).write(this);
        }
        return sexp;
    }

    synchronized void setSexp(final Sexp sexp) {
        if (this.sexp == null)
            this.sexp = sexp;
    }
}
