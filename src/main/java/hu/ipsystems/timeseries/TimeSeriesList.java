package hu.ipsystems.timeseries;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.StreamSupport;

public class TimeSeriesList {

    List<TimeSeries> list = new LinkedList<>();


    private TimeSeriesList() {
    }


    public static TimeSeriesList of(Iterable<TimeSeries> set) {
        TimeSeriesList instance = new TimeSeriesList();
        set.forEach(instance.list::add);
        return instance;
    }

    public List<TimeSeries> getList() {
        return list;
    }

    public TimeSeries sum(ZonedDateTime from, ZonedDateTime to) {
        // cleaner way? :)
        TemporalUnit shortestTemporalUnit = StreamSupport.stream(Spliterators.spliterator(list, Spliterator.SIZED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false)
                .map(TimeSeries::getUnit)
                .min((o1, o2) -> o1.getDuration().compareTo(o2.getDuration()))
                .get();

        List<Iterator<Double>> iterators = new LinkedList<>();
        list.forEach(timeSeries -> iterators.add(timeSeries.iterator(from, to, shortestTemporalUnit)));

        Double[] data = doSum(iterators, (int) shortestTemporalUnit.between(from, to));
        return new TimeSeries(from, shortestTemporalUnit, data);
    }

    private Double[] doSum(List<Iterator<Double>> iterators, int length) {
        Double[] data = new Double[length];

        for (int i = 0; i < length; i++) {
            data[i] = 0.0;
        }

        for (Iterator<Double> it : iterators) {
            for (int i = 0; i < length; i++) {
                // FIXME -- GAP?
                data[i] += it.next();
            }
        }

        return data;
    }

    public TimeSeries sum() {
        Iterator<TimeSeries> it = list.iterator();
        TimeSeries first = it.next();

        ZonedDateTime begin = first.getBegin();
        ZonedDateTime end = first.getEnd();

        while (it.hasNext()) {
            TimeSeries ts = it.next();
            begin = begin.isBefore(ts.getBegin()) ? begin : ts.getBegin();
            end = end.isAfter(ts.getEnd()) ? end : ts.getEnd();
        }

        return sum(begin, end);
    }
}
