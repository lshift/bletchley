package net.lshift.spki.suiteb.sexpstructs;

import java.math.BigInteger;

import net.lshift.spki.convert.Convert;
import net.lshift.spki.convert.SexpBacked;

/**
 * Serialization format for a raw ECDSA signature
 */
@Convert.ByName("suiteb-p384-ecdsa-signature")
public class EcdsaSignature extends SexpBacked {
    public final BigInteger r;
    public final BigInteger s;

    public EcdsaSignature(
        final BigInteger r,
        final BigInteger s
    ) {
        super();
        this.r = r;
        this.s = s;
    }
}
