package net.lshift.spki.suiteb;

import static net.lshift.spki.suiteb.proto.ProtobufHelper.toBigInteger;
import static net.lshift.spki.suiteb.proto.ProtobufHelper.toDigest;

import com.google.protobuf.Message;

import net.lshift.bletchley.suiteb.proto.SuiteBProto;
import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.Convert;
import net.lshift.spki.suiteb.sexpstructs.EcdsaSignature;

/**
 * An SPKI signature, including the digest of the target object and
 * the id of the signing key.
 */
@Convert.ByPosition(name="signature",
    fields={"digest", "keyId", "rawSignature"})
public class Signature implements SequenceItem {
    public final DigestSha384 digest;
    public final DigestSha384 keyId;
    public final EcdsaSignature rawSignature;

    public Signature(
        final DigestSha384 digest,
        final DigestSha384 keyId,
        final EcdsaSignature rawSignature
    ) {
        this.digest = digest;
        this.keyId = keyId;
        this.rawSignature = rawSignature;
    }

    @Override
    public <ActionType extends Message> void process(final InferenceEngine<ActionType> engine, final Condition trust, Class<ActionType> actionType)
        throws InvalidInputException {
        final PublicSigningKey pKey = engine.getPublicSigningKey(keyId);
        if (pKey == null) {
            return;
        }
        if (!pKey.validate(digest, rawSignature))
            throw new CryptographyException("Sig validation failure");
        engine.addItemTrust(digest, engine.getItemTrust(keyId));
    }

    public static Signature fromProtobuf(SuiteBProto.Signature signature) throws InvalidInputException {
        return new Signature(
                toDigest(signature.getDigest()), 
                toDigest(signature.getKeyId()),
                new EcdsaSignature(
                        toBigInteger(signature.getSignature().getR()), 
                        toBigInteger(signature.getSignature().getS())));
    }
    
    public SuiteBProto.SequenceItem.Builder toProtobufSequenceItem() {
        return SuiteBProto.SequenceItem.newBuilder().setSignature(
                SuiteBProto.Signature.newBuilder()
                .setDigest(digest.toProtobufHash())
                .setKeyId(keyId.toProtobufHash())
                .setSignature(rawSignature.toProtobuf()));
    }
}
