package net.lshift.spki.convert;

import static net.lshift.spki.SpkiInputStream.TokenType.ATOM;

import java.io.IOException;
import java.math.BigInteger;

import net.lshift.spki.ParseException;

/**
 * Convert between a BigInteger and a SExp
 */
public class BigIntegerConverter
    implements Converter<BigInteger> {
    @Override
    public void write(ConvertOutputStream out, BigInteger o)
        throws IOException {
        out.atom(o.toByteArray());
    }

    @Override
    public BigInteger read(ConvertInputStream in)
        throws ParseException,
            IOException {
        in.nextAssertType(ATOM);
        return new BigInteger(in.atomBytes());
    }
}
