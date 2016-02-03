package hu.ipsystems.timeseries.time;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

public final class TemporalUnits {

    private TemporalUnits() {
    }

    public static final TemporalUnit QUARTER_HOURS = new TemporalUnit() {

        private final Duration duration = Duration.ofHours(1).dividedBy(4);

        @Override
        public Duration getDuration() {
            return duration;
        }

        @Override
        public boolean isDurationEstimated() {
            return false;
        }

        @Override
        public boolean isDateBased() {
            return false;
        }

        @Override
        public boolean isTimeBased() {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R addTo(R temporal, long amount) {
            return (R) temporal.plus(amount / 4, HOURS).plus((amount % 4) * 15, MINUTES);
        }

        @Override
        public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            if (temporal1Inclusive.getClass() != temporal2Exclusive.getClass()) {
                return temporal1Inclusive.until(temporal2Exclusive, this);
            }

            return temporal1Inclusive.until(temporal2Exclusive, MINUTES) / 15;
        }

        @Override
        public String toString() {
            return "QuarterHours";
        }
    };
}
