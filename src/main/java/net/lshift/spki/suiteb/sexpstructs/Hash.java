package net.lshift.spki.suiteb.sexpstructs;

import net.lshift.spki.convert.Convert;

/**
 * SPKI hash value format
 */
@Convert.ByPosition(name = "hash", fields={"hashType", "value"})
public class Hash {
    public final String hashType;
    public final byte[] value;

    public Hash(final String hashType, final byte[] value) {
        this.hashType = hashType;
        this.value = value;
    }
}
