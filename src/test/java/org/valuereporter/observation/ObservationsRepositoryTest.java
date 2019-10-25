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
import static org.testng.Assert.assertNull;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
public class ObservationsRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(ObservationsRepositoryTest.class);

    ObservationDao observationDao;
    private final static String PREFIX = "ManualTest";

    public ObservationsRepositoryTest(ObservationDao observationDao) {
        this.observationDao = observationDao;
    }

    @Test
    public static void testObservationsRepositoryAddAndPersist() {
        ObservationDao observationDaoMock = mock(ObservationDao.class);
        ObservationsRepository repository = new ObservationsRepository(observationDaoMock);
        repository.updateStatistics(PREFIX, 1L, (ArrayList<ObservedMethodJson>) observedMethodsStubs());
        PrefixCollection prefixCollection = repository.getCollection(PREFIX,1L);
        List<ObservedInterval> intervalls = prefixCollection.getIntervals();
        assertEquals(intervalls.size(),1);
        repository.persistAndResetStatistics(PREFIX,1L);
        verify(observationDaoMock).updateStatistics(eq(PREFIX), eq(intervalls));
    }

    @Test
    public static void verifyCollectionIsCleared() {
        ObservationDao observationDaoMock = mock(ObservationDao.class);
        ObservationsRepository repository = new ObservationsRepository(observationDaoMock);
        repository.updateStatistics(PREFIX, 1L, (ArrayList<ObservedMethodJson>) observedMethodsStubs());
        PrefixCollection prefixCollection = repository.getCollection(PREFIX,1L);
        log.debug("Collection size before {}", prefixCollection.getIntervals().size());
        List<ObservedInterval> intervalls = prefixCollection.getIntervals();
        assertEquals(intervalls.size(), 1);
        repository.persistAndResetStatistics(PREFIX,1L);
        assertNull(repository.getCollection(PREFIX,1L));
    }

    private static List<ObservedMethodJson> observedMethodsStubs() {
        List<ObservedMethodJson> observedMethods = new ArrayList<>();
        long end = System.currentTimeMillis();
        long start = new DateTime(end).minusMillis(50).getMillis();
        observedMethods.add((ObservedMethodJson) new ObservedMethod(PREFIX, "firstMethod", start, end));
        return observedMethods;
    }
}
