package org.valuereporter.statistics;

/**
 * Created by baardl on 05.06.14.
 */
public class ShowUnusedMethodsDao {

    private static final String selectDistinctObservations = "SELECT DISTINCT  methodName FROM ObservedMethod where serviceName like ?";
}
