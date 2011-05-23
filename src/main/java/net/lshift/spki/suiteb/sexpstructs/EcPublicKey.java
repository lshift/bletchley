package net.lshift.spki.suiteb.sexpstructs;

import net.lshift.spki.convert.PositionBeanConvertible;
import net.lshift.spki.suiteb.Ec;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

/**
 * Superclass for serialization formats for EC public keys
 */
public abstract class EcPublicKey
    extends PositionBeanConvertible {
    public final ECPoint point;

    public EcPublicKey(ECPoint point) {
        this.point = point;
    }

    public EcPublicKey(ECPublicKeyParameters params) {
        this(params.getQ());
    }

    public EcPublicKey(AsymmetricCipherKeyPair keyPair) {
        this((ECPublicKeyParameters) keyPair.getPublic());
    }

    public ECPublicKeyParameters getParameters() {
        return Ec.toECPublicKeyParameters(point);
    }

    static {
        Point.ensureRegistered();
    }
}
