package hu.ipsystems.timeseries.util;

import com.google.common.base.Stopwatch;
import hu.ipsystems.timeseries.TimeSeries;
import hu.ipsystems.timeseries.TimeSeriesList;
import hu.ipsystems.timeseries.math.IllegalValueStrategies;

import static hu.ipsystems.timeseries.util.TimeSeriesUtil.asOld;

public class PerfUtil {

    public static TimeSeries measureNewSum(TimeSeriesList list) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        TimeSeries sum1 = list.sum();
        stopwatch.stop();
        System.out.println("New: " + stopwatch);

        return sum1;
    }

    public static hu.ipsystems.timeseries.data.TimeSeries measureOldSum(TimeSeriesList list) {
        Stopwatch stopwatch;
        hu.ipsystems.timeseries.data.TimeSeries first = asOld(list.getList().get(0));
        hu.ipsystems.timeseries.data.TimeSeries[] others = new hu.ipsystems.timeseries.data.TimeSeries[list.getList().size() - 1];
        int i = 0;
        for (TimeSeries ts : list.getList().subList(1, others.length + 1)) {
            others[i++] = asOld(ts);
        }

        stopwatch = Stopwatch.createStarted();
        hu.ipsystems.timeseries.data.TimeSeries sum2 = first.add(IllegalValueStrategies.GAP_to_ZERO, others);
        stopwatch.stop();
        System.out.println("Old: " + stopwatch);
        return sum2;
    }
}
