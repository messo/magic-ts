package hu.ipsystems.timeseries;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TimeSeries implements Iterable<Double> {

    // FIXME -- ???
    static final double GAP = Double.NEGATIVE_INFINITY;

    private final ZonedDateTime begin;
    private final ZonedDateTime end;
    private final TemporalUnit unit;
    private final double[] data;


    public TimeSeries(ZonedDateTime begin, TemporalUnit unit, double[] data) {
        this.begin = begin;
        this.end = begin.plus(data.length, unit);
        this.unit = unit;
        this.data = data;
    }


    private int getIndex(ZonedDateTime time) {
        return (int) unit.between(begin, time);
    }

    private double getData(int idx) {
        return (idx >= 0 && idx < data.length) ? data[idx] : GAP;
    }

    @Override
    public Iterator<Double> iterator() {
        return new TimeSeriesIterator(begin, end);
    }

    public Iterator<Double> iterator(ZonedDateTime begin, ZonedDateTime end) {
        return new TimeSeriesIterator(begin, end);
    }


    private class TimeSeriesIterator implements Iterator<Double> {

        private int cursor;
        private int end;

        public TimeSeriesIterator(ZonedDateTime begin, ZonedDateTime end) {
            this.cursor = getIndex(begin);
            this.end = getIndex(end);
        }

        @Override
        public boolean hasNext() {
            return cursor != end;
        }

        @Override
        public Double next() {
            int i = cursor;
            if (i >= end) {
                throw new NoSuchElementException();
            }

            ++cursor;

            return getData(i);
        }
    }
}
