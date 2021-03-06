package net.lshift.spki;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

/**
 * SPKI input stream accepting a subset of the "advanced" form of SPKI.
 * There are lots of advanced streams it can't currently parse, but
 * it can parse our current prettyPrinted output.
 */
public class AdvancedSpkiInputStream extends SpkiInputStream {
    private static final int NO_PUSHBACK = -2;
    private SpkiInputStream delegate = null;
    private byte[] atomBytes = null;
    private int pushback = NO_PUSHBACK;

    public AdvancedSpkiInputStream(final InputStream is) {
        super(is);
    }

    private int read() throws IOException {
        if (pushback == NO_PUSHBACK) {
            return is.read();
        }
        final int res = pushback;
        pushback = NO_PUSHBACK;
        return res;
    }

    @Override
    protected TokenType doNext()
        throws IOException,
            ParseException {
        while (true) {
            if (delegate != null) {
                final TokenType res = delegate.next();
                if (res == TokenType.EOF) {
                    delegate = null;
                } else {
                    return res;
                }
            }
            final int next = read();
            switch (next) {
            case ' ': case '\n': case '\r': case '\t': case '\f':
                break;
            case '(':
                return TokenType.OPENPAREN;
            case ')':
                return TokenType.CLOSEPAREN;
            case -1:
                return TokenType.EOF;
            case '1': case '2': case '3': case '4': case '5':
            case '6': case '7': case '8': case '9': case '0':
                final int byteCount = readInteger(next);
                atomBytes = readBytes(byteCount);
                return TokenType.ATOM;
            case '\"':
                atomBytes = readString();
                return TokenType.ATOM;
            case '#':
                atomBytes = readHex();
                return TokenType.ATOM;
            case '|':
                atomBytes = readBase64('|');
                return TokenType.ATOM;
            case '{':
                delegate = transportEncodingDelegate();
                break;
            default:
                if (next < 'a' || next > 'z') {
                    throw tokenException(next);
                }
                final ByteArrayOutputStream r = new ByteArrayOutputStream();
                r.write(next);
                pushback = AcceptSomeBytes.TOKEN.accept(r, is);
                atomBytes = r.toByteArray();
                return TokenType.ATOM;
            }
        }
    }

	private ParseException tokenException(final int next) {
		return new ParseException(MessageFormat.format("Invalid token: {0}", next));
	}

    private SpkiInputStream transportEncodingDelegate()
                    throws ParseException, IOException {
        return new AdvancedSpkiInputStream(new ByteArrayInputStream(
                readBase64('}')));
    }

    private byte[] readBase64(final int terminator)
        throws ParseException,
            IOException {
        final ByteArrayOutputStream s = new ByteArrayOutputStream();
        while (true) {
            final int last = AcceptSomeBytes.BASE64.accept(s, is);
            switch (last) {
            case ' ': case '\n': case '\r': case '\t': case '\f':
                break;
            default:
                if (last == terminator) {
                    return Base64.decode(s.toByteArray());
                }
                throw tokenException(last);
            }
        }
    }

    private byte[] readHex()
        throws ParseException,
            IOException {
        final ByteArrayOutputStream s = new ByteArrayOutputStream();
        final int last = AcceptSomeBytes.HEX.accept(s, is);
        if (last != '#') {
            throw tokenException(last);
        }
        return Hex.decode(s.toByteArray());
    }

    private byte[] readString()
        throws ParseException,
            IOException {
        final ByteArrayOutputStream s = new ByteArrayOutputStream();
        while (true) {
            final int last = AcceptSomeBytes.STRING.accept(s, is);
            switch (last) {
            case '\"':
                return s.toByteArray();
            case '\\':
                final int c = is.read();
                // FIXME: handle other backslash escapes
                switch (c) {
                case '\"':
                case '\\':
                    s.write(c);
                    break;
                default:
                    throw new ParseException("Unknown backslash escape: " + c);
                }
                break;
            default:
                throw new ParseException("Can't handle token: " + last);
            }
        }
    }

    @Override
    protected byte[] doAtomBytes()
        throws IOException,
            ParseException {
        if (delegate != null) {
            return delegate.atomBytes();
        }
        final byte[] res = atomBytes;
        atomBytes = null;
        return res;
    }
}
