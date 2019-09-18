package net.lshift.spki.suiteb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import net.lshift.bletchley.suiteb.proto.SuiteBProto;
import net.lshift.spki.InvalidInputException;

/**
 * A SequenceItem encrypted with AES/GCM.
 */
public class AesPacket implements SequenceItem {
    private static final Logger LOG
    = LoggerFactory.getLogger(AesPacket.class);

    public final AesKeyId keyId;
    public final byte[] nonce;
    public final byte[] ciphertext;

    public AesPacket(final AesKeyId keyId,
                     final byte[] nonce,
                     final byte[] ciphertext) {
        this.keyId = keyId;
        this.nonce = nonce;
        this.ciphertext = ciphertext;
    }

    @Override
    public void process(
            final InferenceEngine engine, 
            final Condition trust)
                    throws InvalidInputException {
        final AesKey key = engine.getAesKey(keyId);
        if (key != null) {
            final SequenceItem contents = key.decrypt(this, engine.getParser());
            LOG.debug("Decryption successful");
            engine.process(contents, trust);
        } else {
            LOG.debug("Key not known {}", keyId);
        }
    }

    @Override
    public SuiteBProto.SequenceItem.Builder toProtobuf() {
        return SuiteBProto.SequenceItem.newBuilder().setAesPacket(
                SuiteBProto.AesPacket.newBuilder()
                .setKeyId(keyId.toProtobuf())
                .setNonce(ByteString.copyFrom(nonce))
                .setCiphertext(ByteString.copyFrom(ciphertext)));
    }
}
