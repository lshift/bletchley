package net.lshift.spki.suiteb.demo;

import static net.lshift.spki.convert.openable.OpenableUtils.write;
import static net.lshift.spki.suiteb.InferenceVariables.setNow;
import static net.lshift.spki.suiteb.SequenceUtils.sequence;

import java.io.IOException;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ReadInfo;
import net.lshift.spki.convert.openable.ByteOpenable;
import net.lshift.spki.convert.openable.Openable;
import net.lshift.spki.convert.openable.OpenableUtils;
import net.lshift.spki.suiteb.InferenceEngine;
import net.lshift.spki.suiteb.SequenceItem;

public class Utilities {
    public static final ReadInfo R = ReadInfo.BASE.extend(Service.class);

    public static SequenceItem read(Openable message) throws IOException,
            InvalidInputException {
        return OpenableUtils.read(R, message);
    }

    public static InferenceEngine newEngine() {
        final InferenceEngine engine = new InferenceEngine(R);
        setNow(engine);
        return engine;
    }

    public static ByteOpenable emptyByteOpenable() {
        try {
            ByteOpenable res = new ByteOpenable();
            write(res, sequence());
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Openable asOpenable(SequenceItem sequence) throws IOException {
        final ByteOpenable target = new ByteOpenable();
        write(target, sequence);
        return target;
    }
}
