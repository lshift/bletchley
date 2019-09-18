package net.lshift.spki.suiteb.fingerprint;

import static net.lshift.spki.suiteb.SequenceUtils.action;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.google.protobuf.ByteString;

import net.lshift.bletchley.suiteb.proto.SimpleMessageProto.SimpleMessage;
import net.lshift.spki.suiteb.Action;
import net.lshift.spki.suiteb.SequenceItem;

@RunWith(Theories.class)
public class FingerprintStabilityTest {

    private static class TestPair {
        private final SequenceItem test;
        private final String fingerprint;

        public TestPair(final SequenceItem test, final String fingerprint) {
            this.test = test;
            this.fingerprint = fingerprint;
        }

        public SequenceItem getTest() {
            return test;
        }

        public String getFingerprint() {
            return fingerprint;
        }
    }

    @DataPoints
    public static TestPair[] data() {
        return new TestPair[] {
            new TestPair(message(""),
                "lara-denial-piano/ce-pardon-spade/wax-flu-rash/doris-morsel-trail/mice-spray-beers"),
            new TestPair(message("hello"),
                "scour-fault-icons/skin-teacup-sizes/kq-tend-datum/spitz-say-sank/truck-drawn-crops")
        };
    }

    private static Action message(String text) {
        return action(SimpleMessage.newBuilder().setType("").setContent(ByteString.copyFromUtf8(text)).build());
    }

    @Theory
    public void theoryFingerprintsAreStable(final TestPair pair) {
        assertThat(
            FingerprintUtils.getFingerprint(pair.getTest()),
            is(pair.getFingerprint()));
    }
}


