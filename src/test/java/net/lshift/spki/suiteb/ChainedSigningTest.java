package net.lshift.spki.suiteb;

import static net.lshift.spki.suiteb.InferenceEngineTest.checkMessage;
import static net.lshift.spki.suiteb.RoundTrip.roundTrip;
import static net.lshift.spki.suiteb.SequenceUtils.sequence;
import static net.lshift.spki.suiteb.Signed.signed;

import java.util.Collections;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.UsesSimpleMessage;

import org.junit.Test;

public class ChainedSigningTest extends UsesSimpleMessage
{
    @Test
    public void testSequenceBasedSigningAndVerification() throws InvalidInputException {
        PrivateSigningKey privateKey = PrivateSigningKey.generate();
        privateKey = roundTrip(PrivateSigningKey.class, privateKey);
        final PublicSigningKey publicKey = privateKey.getPublicKey();
        final Action message = makeMessage();
        final Sequence subsequence = sequence(
            DigestSha384.digest(message),
            publicKey.keyId // Some rubbish
        );
        Sequence sequence = sequence(
            publicKey,
            privateKey.sign(subsequence),
            signed(subsequence),
            signed(message));
        sequence = roundTrip(Sequence.class, sequence);

        final InferenceEngine inference = new InferenceEngine();
        inference.processTrusted(new Cert(publicKey.getKeyId(), Collections.<Condition>emptyList()));
        inference.process(sequence);
        checkMessage(inference, message);
    }
}
