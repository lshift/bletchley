package net.lshift.spki.suiteb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.UsesSimpleMessage;
import net.lshift.spki.suiteb.sexpstructs.Sequence;
import net.lshift.spki.suiteb.sexpstructs.SequenceItem;
import net.lshift.spki.suiteb.simplemessage.SimpleMessage;

import org.junit.Test;

public class InferenceEngineTest extends UsesSimpleMessage {
    @Test
    public void emptyListIfSignerHasDoneNothing() {
        final PrivateSigningKey key = PrivateSigningKey.generate();
        final InferenceEngine engine = new InferenceEngine();
        engine.addTrustedKey(key.getPublicKey().getKeyId());
        final List<ActionType> res = engine.getActions();
        assertThat(res.size(), is(equalTo(0)));
    }

    @Test
    public void emptyListIfNotSignedByTrustedKey() throws InvalidInputException {
        final PrivateSigningKey key = PrivateSigningKey.generate();
        final InferenceEngine engine = new InferenceEngine();
        engine.process(key.getPublicKey());
        final Action message = SimpleMessage.makeMessage(this.getClass());
        engine.process(key.sign(message));
        engine.process(message);
        final List<ActionType> res = engine.getActions();
        assertThat(res.size(), is(equalTo(0)));
    }

    @Test
    public void foundIfSignedByTrustedKey() throws InvalidInputException {
        final Action message = SimpleMessage.makeMessage(this.getClass());
        final PrivateSigningKey key = PrivateSigningKey.generate();
        final InferenceEngine engine = new InferenceEngine();
        engine.addTrustedKey(key.getPublicKey().getKeyId());
        engine.process(key.getPublicKey());
        engine.process(key.sign(message));
        engine.process(message);
        final List<ActionType> res = engine.getActions();
        assertThat(res.size(), is(equalTo(1)));
        assertThat(res.get(0), is(equalTo(message.getPayload())));
    }

    @Test
    public void foundEvenInsideEncryptedBit() throws InvalidInputException {
        final Action message = SimpleMessage.makeMessage(this.getClass());
        final PrivateSigningKey key = PrivateSigningKey.generate();
        final AesKey aeskey = AesKey.generateAESKey();
        final InferenceEngine engine = new InferenceEngine();
        engine.addTrustedKey(key.getPublicKey().getKeyId());
        engine.process(key.getPublicKey());
        engine.process(aeskey);
        final AesPacket encrypted = aeskey.encrypt(message);
        engine.process(key.sign(encrypted));
        engine.process(encrypted);
        final List<ActionType> res = engine.getActions();
        assertThat(res.size(), is(equalTo(1)));
        assertThat(res.get(0), is(equalTo(message.getPayload())));
    }

    @Test
    public void foundIfBlindlyTrusting() throws InvalidInputException {
        final Action message = SimpleMessage.makeMessage(this.getClass());
        final InferenceEngine engine = new InferenceEngine();
        engine.setBlindlyTrusting(true);
        engine.process(message);
        final List<ActionType> res = engine.getActions();
        assertThat(res.size(), is(equalTo(1)));
        assertThat(res.get(0), is(equalTo(message.getPayload())));
    }

    @Test
    public void foundIfEncryptionKeyProvided() throws InvalidInputException {
        final List<SequenceItem> sequence = new ArrayList<SequenceItem>();
        final PrivateEncryptionKey key = PrivateEncryptionKey.generate();
        sequence.add(key);
        final AesKey aeskey = key.getPublicKey().setupEncrypt(sequence);
        final Action message = SimpleMessage.makeMessage(this.getClass());
        sequence.add(aeskey.encrypt(message));
        final InferenceEngine engine = new InferenceEngine();
        engine.setBlindlyTrusting(true);
        engine.process(new Sequence(sequence));
        final List<ActionType> res = engine.getActions();
        assertThat(res.size(), is(equalTo(1)));
        assertThat(res.get(0), is(equalTo(message.getPayload())));
    }
}
