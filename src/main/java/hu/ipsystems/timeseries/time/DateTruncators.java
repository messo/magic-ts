package hu.ipsystems.timeseries.time;

import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

public enum DateTruncators implements TemporalAdjuster {

    TO_MONTH {
        @Override
        public Temporal adjustInto(Temporal temporal) {
            return temporal.with(ChronoField.NANO_OF_DAY, 0).with(ChronoField.DAY_OF_MONTH, 1);
        }
    },

    TO_QUARTER {
        @Override
        public Temporal adjustInto(Temporal temporal) {
            return temporal.with(ChronoField.NANO_OF_DAY, 0).with(IsoFields.DAY_OF_QUARTER, 1);
        }
    },

    TO_YEAR {
        @Override
        public Temporal adjustInto(Temporal temporal) {
            return TO_MONTH.adjustInto(temporal).with(ChronoField.MONTH_OF_YEAR, 1);
        }
    }
}
