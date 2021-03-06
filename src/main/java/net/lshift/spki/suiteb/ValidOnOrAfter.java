package net.lshift.spki.suiteb;

import static net.lshift.spki.suiteb.InferenceVariables.NOW;

import java.util.Date;

import net.lshift.spki.convert.Convert;

/**
 * Valid only on or after the date given -
 * fails before that date.
 */
@Convert.ByPosition(name = "valid-on-or-after", fields = {"date"})
public class ValidOnOrAfter implements Condition {
    public final Date date;

    public ValidOnOrAfter(final Date date) {
        this.date = date;
    }

    @Override
    public boolean allows(final InferenceEngine engine, final ActionType action) {
        return !NOW.get(engine).before(date);
    }
}
