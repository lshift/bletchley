package net.lshift.spki.suiteb.cli;

import static net.lshift.spki.suiteb.SequenceUtils.sequence;
import static net.lshift.spki.suiteb.Signed.signed;
import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.convert.ConverterCatalog;
import net.lshift.spki.suiteb.Action;
import net.lshift.spki.suiteb.AesKey;
import net.lshift.spki.suiteb.EncryptionCache;
import net.lshift.spki.suiteb.PrivateEncryptionKey;
import net.lshift.spki.suiteb.PrivateSigningKey;
import net.lshift.spki.suiteb.PublicEncryptionKey;
import net.lshift.spki.suiteb.Sequence;
import net.lshift.spki.suiteb.simplemessage.SimpleMessage;

/**
 * Quick and dirty speed test visible from the CLI.
 */
public class SpeedTester {
    private static final String MESSAGE_TYPE = "speed-test-message";
    private static final ConverterCatalog R = ConverterCatalog.BASE.extend(SimpleMessage.class);
    private final PrivateSigningKey privateKey;
    private final byte[] publicKeyBytes;
    private final byte[] messageBytes;

    public SpeedTester() {
        this.privateKey = PrivateSigningKey.generate();
        this.publicKeyBytes = ConvertUtils.toBytes(
            PrivateEncryptionKey.generate().getPublicKey());
        this.messageBytes = new byte[100];
    }

    public void speedTest() throws InvalidInputException {
        doRun();
        final long start = System.currentTimeMillis();
        for (int i = 0; ; i++) {
            final long end = System.currentTimeMillis();
            if (end - start > 5000 && i > 0) {
                System.out.println("Time (ms): " + (end-start)*1.0/i);
                break;
            }
            doRun();
        }
    }

    private void doRun() throws InvalidInputException {
        final AesKey aesKey = AesKey.generateAESKey();
        final PublicEncryptionKey pKey
            = ConvertUtils.fromBytes(R, PublicEncryptionKey.class, publicKeyBytes);
        final EncryptionCache ephemeral = EncryptionCache.ephemeralKey();
        final Action message = new Action(new SimpleMessage(
            MESSAGE_TYPE, messageBytes));

        final Sequence sequence = sequence(
            ephemeral.getPublicKey(),
            ephemeral.encrypt(pKey, aesKey),
            aesKey.encrypt(sequence(
                privateKey.sign(message),
                signed(message)
        )));
        ConvertUtils.toBytes(sequence);
    }

}
