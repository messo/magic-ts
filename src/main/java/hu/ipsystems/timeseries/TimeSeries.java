package hu.ipsystems.timeseries;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static hu.ipsystems.timeseries.time.Temporals.roundDown;
import static hu.ipsystems.timeseries.time.Temporals.roundUp;

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


    public ZonedDateTime getBegin() {
        return begin;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public TemporalUnit getUnit() {
        return unit;
    }

    public double[] getData() {
        return Arrays.copyOf(data, data.length);
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


    private int getIndex(ZonedDateTime time) {
        return (int) unit.between(begin, time);
    }

    private double getData(int idx) {
        return (idx >= 0 && idx < data.length) ? data[idx] : GAP;
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
        private final int lastIterationEnd;

        private ZonedDateTime nextIterationAt;
        private int cursor;
        private int turnPoint;
        private Double lastRet;

        public TimeSeriesIteratorWithShorterTemporalUnit(ZonedDateTime start, ZonedDateTime finish, TemporalUnit newIterationUnit) {
            ZonedDateTime innerStart = roundDown(start, unit);
            ZonedDateTime innerEnd = roundUp(finish, unit);
            this.iterator = new TimeSeriesIterator(innerStart, innerEnd);
            this.newIterationUnit = newIterationUnit;

            this.nextIterationAt = innerStart.plus(1, unit);
            this.cursor = (int) newIterationUnit.between(innerStart, start);
            this.turnPoint = (int) newIterationUnit.between(innerStart, nextIterationAt);
            int end = (int) newIterationUnit.between(roundDown(finish, unit), finish);
            if (end == 0) {
                // we need to finish at the very last item, without starting a new iteration
                end = (int) newIterationUnit.between(finish.minus(1, unit), finish);
            }
            this.lastIterationEnd = end;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext() || (cursor != lastIterationEnd);
        }

        @Override
        public Double next() {
            if (lastRet == null) {
                lastRet = iterator.next();
            } else if (cursor >= turnPoint) {
                cursor = 0;
                ZonedDateTime current = nextIterationAt;
                nextIterationAt = nextIterationAt.plus(1, unit);
                turnPoint = (int) newIterationUnit.between(current, nextIterationAt);
                lastRet = iterator.next();
            }

            ++cursor;

            return lastRet;
        }
    }
}
