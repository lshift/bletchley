package net.lshift.spki.convert;

import static net.lshift.spki.sexpform.Create.atom;
import static net.lshift.spki.sexpform.Create.list;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.sexpform.Sexp;

import org.junit.Test;

public class ConvertTest extends UsesCatalog
{
    @Test
    public void convertTest() throws InvalidInputException {
        final ConvertExample test = new ConvertExample(
            BigInteger.valueOf(3), BigInteger.valueOf(17), "test");
        testExample(test, ConvertExample.class);
    }

    @Test
    public void convertByNameTest() throws InvalidInputException {
        final ByNameConvertExample test = new ByNameConvertExample(
            BigInteger.valueOf(3), BigInteger.valueOf(17), "test", Arrays.asList("a", "b", "c"));
        testExample(test, ByNameConvertExample.class);
    }

    private <T> void testExample(final T test, Class<T> clazz)
            throws InvalidInputException {
        final byte[] bytes = ConvertUtils.toBytes(test);
        //PrettyPrinter.prettyPrint(System.out, sexp);
        final T changeBack = ConvertUtils.fromBytes(getReadInfo(),
            clazz, bytes);
        assertEquals(test, changeBack);
    }

    @Test
    public void sexpTest() throws InvalidInputException {
        final byte[] bytes = ConvertUtils.bytes("(3:foo)");
        assertEquals(list("foo"),
            ConvertUtils.fromBytes(getReadInfo(), Sexp.class, bytes));
    }

    @Test(expected=ConvertException.class)
    public void extraBytesMeansParseException() throws InvalidInputException {
        final byte[] bytes = ConvertUtils.bytes("(3:foo)1:o");
        ConvertUtils.fromBytes(getReadInfo(), Sexp.class, bytes);
    }

    @Test
    public void marshalTest() {
        final byte[] bytes = "(4:test26:abcdefghijklmnopqrstuvwxyz5:123455::: ::)".getBytes(StandardCharsets.US_ASCII);
        final Sexp struct = list("test", atom("abcdefghijklmnopqrstuvwxyz"), atom("12345"), atom(":: ::"));
        assertArrayEquals(bytes, ConvertUtils.toBytes(struct));
    }

    @Test
    public void unmarshalTest() throws InvalidInputException {
        final byte[] bytes = "(4:test26:abcdefghijklmnopqrstuvwxyz5:123455::: ::)".getBytes(StandardCharsets.US_ASCII);
        final Sexp struct = list("test", atom("abcdefghijklmnopqrstuvwxyz"), atom("12345"), atom(":: ::"));
        assertEquals(struct, ConvertUtils.fromBytes(getReadInfo(), Sexp.class, bytes));
    }

    @Test
    public void convertFromUUID() {
        final String uidstring = "093fe929-3d5d-48f9-bb41-58A382DE934F";
        final UUID uuid = UUID.fromString(uidstring);
        final Sexp uBytes = ConverterCache.getConverter(UUID.class).write(uuid);
        // UUID converter forces the lower case representation of UUID
        assertEquals(atom(uidstring.toLowerCase()), uBytes);
    }

    @Test
    public void convertToUUID() throws InvalidInputException {
        final String uidstring = "093fe929-3d5d-48f9-bb41-58A382DE934F";
        final byte[] uBytes = ConvertUtils.toBytes(atom(uidstring));
        assertEquals(UUID.fromString(uidstring),
            ConvertUtils.fromBytes(getReadInfo(), UUID.class, uBytes));
    }

    @Test
    public void convertHyphenTest() throws InvalidInputException, IOException {
        final ConvertExample test = new ConvertExample(
            BigInteger.valueOf(3), BigInteger.valueOf(17), "-");
        testExample(test, ConvertExample.class);
    }

}
