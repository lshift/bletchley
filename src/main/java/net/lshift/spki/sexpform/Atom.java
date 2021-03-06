package net.lshift.spki.sexpform;

import java.util.Arrays;

import net.lshift.spki.convert.ConvertException;

/**
 * An atom in an SPKI S-expression
 */
public final class Atom extends Sexp {
    private final byte[] bytes;

    public Atom(final byte[] bytes) {
        if(bytes == null) {
                throw new IllegalArgumentException("bytes == null");
        }
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public Atom atom()
        throws ConvertException {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Atom other = (Atom) obj;

        return Arrays.equals(bytes, other.bytes);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (final byte b: bytes) {
            if (Character.isLetterOrDigit(b) || b == (byte) '-') {
                sb.append((char) b);
            } else {
                sb.append(String.format("\\0x%02x", b));
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
