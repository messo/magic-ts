package hu.ipsystems.timeseries;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TimeSeries implements Iterable<Double> {

    // FIXME -- ???
    static final double GAP = 0.0;

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

    public double[] getData() {
        return data;
    }

    @Override
    public Iterator<Double> iterator() {
        return new TimeSeriesIterator(begin, end);
    }

    public Iterator<Double> iterator(ZonedDateTime begin, ZonedDateTime end) {
        return new TimeSeriesIterator(begin, end);
    }

    public Iterator<Double> iterator(ZonedDateTime begin, ZonedDateTime end, TemporalUnit unit) {
        int compareTo = unit.getDuration().compareTo(this.unit.getDuration());
        if (compareTo == 0) {
            return new TimeSeriesIterator(begin, end);
        } else if (compareTo == -1) {
            return new TimeSeriesIteratorWithShorterTemporalUnit(begin, end, unit);
        } else {
            throw new IllegalArgumentException("Iteration cannot be done in larger steps than the resolution.");
        }
    }

    public ZonedDateTime getBegin() {
        return begin;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public TemporalUnit getUnit() {
        return unit;
    }

    private class TimeSeriesIterator implements Iterator<Double> {

        private final int end;

        private int cursor;

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

    private class TimeSeriesIteratorWithShorterTemporalUnit implements Iterator<Double> {

        private final TimeSeriesIterator iterator;
        private final int mod;
        private final int end;

        private int cursor;
        private double lastRet;

        public TimeSeriesIteratorWithShorterTemporalUnit(ZonedDateTime start, ZonedDateTime finish, TemporalUnit newIterationUnit) {
            // FIXME -- REMOVE once it's fixed!
            Preconditions.checkArgument(unit == ChronoUnit.DAYS);

            // FIXME -- these are working only for DAYS!
            ZonedDateTime innerStart = start.truncatedTo(unit);
            ZonedDateTime innerEnd = finish.truncatedTo(unit).plusDays(1);
            this.iterator = new TimeSeriesIterator(innerStart, innerEnd);

            this.cursor = (int) newIterationUnit.between(TimeSeries.this.begin, start);
            this.mod = 24; // FIXME -- only four DAYS -> HOURS, 23-25 hours!
            this.end = mod - (int) newIterationUnit.between(finish, innerEnd);

            this.lastRet = iterator.next();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext() || (cursor != end);
        }

        @Override
        public Double next() {
            if (cursor >= mod) {
                cursor = 1;
                lastRet = iterator.next();
            } else {
                ++cursor;
            }

            return lastRet;
        }
    }
}
