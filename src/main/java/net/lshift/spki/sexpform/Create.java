package net.lshift.spki.sexpform;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Static methods useful for creating S-expression structures.
 * Designed to be imported statically.
 */
public class Create {
	private Create() {
		// This class cannot be instantiated
	}
	
    public static Atom atom(final byte[] bytes) {
        return new Atom(bytes);
    }

    public static Atom atom(final String name) {
        return atom(name.getBytes(StandardCharsets.UTF_8));
    }

    public static Slist list(final String head, final Sexp... tail) {
        return new Slist(atom(head), tail);
    }

    public static Sexp list(final String name, final List<Sexp> tail) {
        return list(name, tail.toArray(new Sexp[tail.size()]));
    }
}
