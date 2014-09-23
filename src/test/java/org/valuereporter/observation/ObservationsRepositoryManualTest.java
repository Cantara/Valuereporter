package org.valuereporter.observation;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
public class ObservationsRepositoryManualTest {
    private static final Logger log = LoggerFactory.getLogger(ObservationsRepositoryManualTest.class);

    ObservationDao observationDao;
    private final static String PREFIX = "ManualTest";

    public ObservationsRepositoryManualTest(ObservationDao observationDao) {
        this.observationDao = observationDao;
    }

    public static void main(String[] args) {
        testObservationsRepositoryAddAndPersist();

        //TODO add and persist with Spring see http://blog.frankel.ch/database-unit-testing-with-dbunit-spring-and-testng
    }

    @Test
    public static void testObservationsRepositoryAddAndPersist() {
        ObservationDao observationDaoMock = mock(ObservationDao.class);
        ObservationsRepository repository = new ObservationsRepository(observationDaoMock);
        repository.updateStatistics(PREFIX,observedMethodsStubs());
        PrefixCollection prefixCollection = repository.getCollection(PREFIX);
        List<ObservedInterval> intervalls = prefixCollection.getIntervalls();
        assertEquals(intervalls.size(),1);
        repository.persistStatistics(PREFIX);
        verify(observationDaoMock).updateStatistics(eq(PREFIX),eq(intervalls));
    }

    private static List<ObservedMethod> observedMethodsStubs() {
        List<ObservedMethod> observedMethods = new ArrayList<>();
        long end = System.currentTimeMillis();
        long start = new DateTime(end).minusMillis(50).getMillis();
        observedMethods.add(new ObservedMethod(PREFIX,"firstMethod",start, end));
        return observedMethods;
    }
}
