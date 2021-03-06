package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class BooleanSeries_SortTest {

    @Test
    public void testSort() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true, false)
                .sort((b1, b2) -> b1 == b2 ? 0 : b1 ? -1 : 1);

        new SeriesAsserts(s).expectData(true, true, false, false);
    }
}
