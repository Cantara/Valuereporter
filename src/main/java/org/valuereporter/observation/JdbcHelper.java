package org.valuereporter.observation;

import java.sql.Timestamp;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class JdbcHelper {


    protected static String insertObservedInterval(String serviceName, String methodName, long timestamp, Object[] values) {
        Timestamp sqlDate = new Timestamp(timestamp);
        int duration = 15*60*1000;
        long count = (Long)values[0];
        double max = (Double)values[1]; //snapshot.getMax()),
        double mean = (Double)values[2]; //       convertDuration(snapshot.getMean()),
        double min = (Double)values[3]; //convertDuration(snapshot.getMin()),
        double standardDeviation = (Double)values[4]; //convertDuration(snapshot.getStdDev()),
        double median = (Double)values[5]; //convertDuration(snapshot.getMedian()),
        //snapshot.get75thPercentile()
        double p95 = (Double)values[7]; //convertDuration(snapshot.get95thPercentile()),
        double p98 = (Double)values[8]; //convertDuration(snapshot.get98thPercentile()),
        double p99 = (Double)values[9]; //convertDuration(snapshot.get99thPercentile()),
        String sql = "insert into ObservedInterval (observedKeysId, startTime, duration, vrCount, vrMax, vrMin, vrMean, vrMedian, stdDev, p95, p98, p99)\n" +
                "  select o.id, '" + sqlDate + "', "+duration +","+ count +"," + max +"," +min + "," + mean + "," + median + "," + standardDeviation +
                "," + p95+"," + p98+"," + p99+ "\n" +
                "  from ObservedKeys o\n" +
                "  where serviceName='" + serviceName + "' and methodName = '" + methodName + "';";
        return sql;
    }
}
