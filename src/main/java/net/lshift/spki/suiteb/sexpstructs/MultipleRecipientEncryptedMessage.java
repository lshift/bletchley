package net.lshift.spki.suiteb.sexpstructs;

import net.lshift.spki.convert.P;
import net.lshift.spki.convert.PositionBeanConvertable;
import net.lshift.spki.convert.SExpName;

/**
 * A message encrypted for multiple recipients
 */
public class MultipleRecipientEncryptedMessage
    extends PositionBeanConvertable
{
    public final EncryptionRecipients recipients;
    // FIXME: replace ciphertext with a structure including a nonce
    public final byte[] ciphertext;

    @SExpName("suiteb-multiple-recipients")
    public MultipleRecipientEncryptedMessage(
        @P("recipients") EncryptionRecipients recipients,
        @P("ciphertext") byte[] ciphertext)
    {
        super();
        this.recipients = recipients;
        this.ciphertext = ciphertext;
    }
}