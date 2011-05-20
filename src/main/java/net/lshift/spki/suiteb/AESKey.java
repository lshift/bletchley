package net.lshift.spki.suiteb;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import net.lshift.spki.Constants;
import net.lshift.spki.Marshal;
import net.lshift.spki.ParseException;
import net.lshift.spki.convert.Convert;
import net.lshift.spki.convert.P;
import net.lshift.spki.convert.PositionBeanConvertible;
import net.lshift.spki.convert.SExpName;
import net.lshift.spki.suiteb.sexpstructs.SequenceItem;

/**
 * A key to use with AES/GCM.
 */
public class AESKey extends PositionBeanConvertible implements SequenceItem
{
    public static final int AES_KEY_BYTES = 32;
    private static final byte[] KEYID_AD
        = "8:keyid-ad".getBytes(Constants.UTF8);
    private static final byte[] ZERO_BYTES = new byte[] { };

    public final byte[] key;

    @SExpName("aes-gcm-key")
    public AESKey(
        @P("key") byte[] key
    ) {
        this.key = key;
    }

    public AESKeyId getKeyId() {
        try {
            GCMBlockCipher gcm = new GCMBlockCipher(new AESFastEngine());
            gcm.init(true, new AEADParameters(
                new KeyParameter(key), 128, KEYID_AD, KEYID_AD));
            byte[] ciphertext = new byte[gcm.getOutputSize(ZERO_BYTES.length)];
            int resp = gcm.processBytes(ZERO_BYTES, 0, ZERO_BYTES.length,
                ciphertext, 0);
            gcm.doFinal(ciphertext, resp);
            return new AESKeyId(ciphertext);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    public AESPacket encrypt(SequenceItem message)
    {
        try {
            byte[] nonce = EC.randomBytes(12);
            GCMBlockCipher gcm = new GCMBlockCipher(new AESFastEngine());
            gcm.init(true, new AEADParameters(
                new KeyParameter(key), 128, nonce, ZERO_BYTES));
            byte[] plaintext = Marshal.marshal(
                Convert.toSExp(SequenceItem.class, message));
            byte[] ciphertext = new byte[gcm.getOutputSize(plaintext.length)];
            int resp = gcm.processBytes(plaintext, 0, plaintext.length,
                ciphertext, 0);
            gcm.doFinal(ciphertext, resp);
            return new AESPacket(getKeyId(), nonce, ciphertext);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    public SequenceItem decrypt(AESPacket packet)
        throws InvalidCipherTextException,
            ParseException
    {
        try {
            GCMBlockCipher gcm = new GCMBlockCipher(new AESFastEngine());
            gcm.init(false, new AEADParameters(
                new KeyParameter(key), 128, packet.nonce, ZERO_BYTES));
            byte[] newtext = new byte[
                gcm.getOutputSize(packet.ciphertext.length)];
            int pp = gcm.processBytes(packet.ciphertext, 0,
                packet.ciphertext.length, newtext, 0);
            gcm.doFinal(newtext, pp);
            return Convert.fromSExp(
                SequenceItem.class, Marshal.unmarshal(newtext));
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }

    public static AESKey generateAESKey()
    {
        return new AESKey(EC.randomBytes(AES_KEY_BYTES));
    }
}
