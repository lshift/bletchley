package net.lshift.spki.suiteb;

import java.util.Date;

public class InferenceVariables {
    public static final InferenceVariable<Date> NOW
        = new InferenceVariable<Date>(Date.class, "now");
}
