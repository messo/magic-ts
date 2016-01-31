package hu.ipsystems.timeseries;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static hu.ipsystems.timeseries.time.TemporalUtil.roundDown;
import static hu.ipsystems.timeseries.time.TemporalUtil.roundUp;

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
        private final TemporalUnit newIterationUnit;
        private final int end;

        private ZonedDateTime nextIterationAt;
        private int cursor;
        private int turnPoint;
        private double lastRet;

        public TimeSeriesIteratorWithShorterTemporalUnit(ZonedDateTime start, ZonedDateTime finish, TemporalUnit newIterationUnit) {
            // FIXME -- REMOVE once it's fixed!
            Preconditions.checkArgument(unit == ChronoUnit.DAYS);

            ZonedDateTime innerStart = roundDown(start, unit);
            ZonedDateTime innerEnd = roundUp(finish, unit);
            this.iterator = new TimeSeriesIterator(innerStart, innerEnd);
            this.newIterationUnit = newIterationUnit;

            this.nextIterationAt = innerStart.plus(1, unit);
            int _end = (int) newIterationUnit.between(finish.truncatedTo(unit), finish);
            if (_end == 0) {
                // recalculate
                _end = (int) newIterationUnit.between(finish.minus(1, unit), finish);
            }
            this.end = _end;
            this.turnPoint = (int) newIterationUnit.between(start, nextIterationAt);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext() || (cursor != end);
        }

        @Override
        public Double next() {
            if (cursor == 0) {
                lastRet = iterator.next();
            } else if (cursor >= turnPoint) {
                cursor = 0;
                ZonedDateTime currIterationAt = nextIterationAt;
                nextIterationAt = nextIterationAt.plus(1, unit);
                turnPoint = (int) newIterationUnit.between(currIterationAt, nextIterationAt);
                lastRet = iterator.next();
            }

            ++cursor;

            return lastRet;
        }
    }
}
