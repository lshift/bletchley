package net.lshift.spki;

import static net.lshift.spki.SpkiInputStream.TokenType.ATOM;
import static net.lshift.spki.SpkiInputStream.TokenType.CLOSEPAREN;
import static net.lshift.spki.SpkiInputStream.TokenType.EOF;
import static net.lshift.spki.SpkiInputStream.TokenType.OPENPAREN;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class AdvancedSpkiInputStreamTest extends SpkiInputStreamTest
{
    @Override
    protected void setInput(final InputStream inputStream) {
        sis = new AdvancedSpkiInputStream(inputStream);
    }

    @Test
    public void getBareAtom()
        throws IOException, ParseException {
        setInput("foo");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("foo")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void getQuotedString()
        throws IOException, ParseException {
        setInput("\"foo\"");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("foo")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void getTildeAtom()
        throws IOException, ParseException {
        setInput("\"~foo\"");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("~foo")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void getQuotedStringWithBackslashes() throws ParseException, IOException {
        setInput("\"this\\\"and\\\\that\"");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("this\"and\\that")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void canStillReadOpenCloseParen()
        throws ParseException, IOException {
        setInput(" ( foo ) ");
        assertThat(sis.next(), is(OPENPAREN));
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("foo")));
        assertThat(sis.next(), is(CLOSEPAREN));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void handlePushbackProperly()
        throws ParseException, IOException {
        setInput(" ( foo(bar) ) ");
        assertThat(sis.next(), is(OPENPAREN));
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("foo")));
        assertThat(sis.next(), is(OPENPAREN));
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("bar")));
        assertThat(sis.next(), is(CLOSEPAREN));
        assertThat(sis.next(), is(CLOSEPAREN));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void readHexExpression()
        throws IOException, ParseException {
        setInput("#21#");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("!")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void readBase64Expression()
        throws IOException, ParseException {
        setInput("|TWFu|");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("Man")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void readBase64ExpressionWithSpaces()
        throws IOException, ParseException {
        setInput("| T W F u |");
        assertThat(sis.next(), is(ATOM));
        assertThat(sis.atomBytes(), is(s("Man")));
        assertThat(sis.next(), is(EOF));
    }

    @Test
    public void readTransportEncoding() throws ParseException, IOException {
        assertThat(canonread("base64.spki"), is(canonread("advanced.spki")));
    }

    private static byte[] canonread(final String name) throws ParseException, IOException {
        final ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrettyPrinter.copyStream(
            new AdvancedSpkiInputStream(
                AdvancedSpkiInputStreamTest.class.getResourceAsStream(name)),
            new CanonicalSpkiOutputStream(s));
        return s.toByteArray();
    }

    @Test
    public void HexIgnoresSpaces() {
        assertThat(Hex.decode("  4 d 6 1 6 E "),
            is(s("Man")));
    }

//    @Test
//    public void Base64IgnoresSpaces() {
//        assertThat(Base64.decode("TWFu"),
//            is(s("Man")));
//        assertThat(Base64.decode(" T W F u TWFu"),
//            is(s("ManMan")));
//        assertThat(Base64.decode(" T W F u "), // fails
//            is(s("Man")));
//    }
}
