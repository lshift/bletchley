package net.lshift.spki.convert.openable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.convert.ConverterCatalog;
import net.lshift.spki.suiteb.SequenceItem;

import org.apache.commons.io.IOUtils;

/**
 * Utilities for acting on Openable objects
 */
public class OpenableUtils {
	private OpenableUtils() {
		// This class cannot be instantiated
	}

	public static byte[] readBytes(final Openable message)
        throws IOException {
        final InputStream is = message.read();
        try {
            return IOUtils.toByteArray(is);
        } finally {
            is.close();
        }
    }

    public static void writeBytes(final Openable out, final byte[] messageBytes)
        throws IOException {
        final OutputStream os = out.write();
        try {
            os.write(messageBytes);
        } finally {
            os.close();
        }
    }

    public static <T> T read(
        final ConverterCatalog r,
        final Class<T> clazz,
        final Openable open)
        throws IOException, InvalidInputException {
        final InputStream is = open.read();
        try {
            return ConvertUtils.read(r, clazz, is);
        } finally {
            is.close();
        }
    }

    public static void write(final Openable open,
                             final Object o) throws IOException {
        final OutputStream os = open.write();
        try {
            ConvertUtils.write(o, os);
        } finally {
            os.close();
        }
    }

    public static SequenceItem read(final ConverterCatalog r, final Openable open)
        throws IOException, InvalidInputException {
        return read(r, SequenceItem.class, open);
    }
}
