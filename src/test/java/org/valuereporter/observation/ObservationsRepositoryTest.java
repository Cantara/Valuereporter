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
    private final static String SERVICE_NAME = "ManualTest";

    public ObservationsRepositoryTest(ObservationDao observationDao) {
        this.observationDao = observationDao;
    }

    @Test
    public static void testObservationsRepositoryAddAndPersist() {
        ObservationDao observationDaoMock = mock(ObservationDao.class);
        ObservationsRepository repository = new ObservationsRepository(observationDaoMock);
        repository.updateStatistics(SERVICE_NAME,1L,observedMethodsStubs());
        PrefixCollection serviceNameCollection = repository.getCollection(SERVICE_NAME,1L);
        List<ObservedInterval> intervalls = serviceNameCollection.getIntervals();
        assertEquals(intervalls.size(),1);
        repository.persistAndResetStatistics(SERVICE_NAME,1L);
        verify(observationDaoMock).updateStatistics(eq(SERVICE_NAME), eq(intervalls));
    }

    @Test
    public static void verifyCollectionIsCleared() {
        ObservationDao observationDaoMock = mock(ObservationDao.class);
        ObservationsRepository repository = new ObservationsRepository(observationDaoMock);
        repository.updateStatistics(SERVICE_NAME,1L,observedMethodsStubs());
        PrefixCollection serviceNameCollection = repository.getCollection(SERVICE_NAME,1L);
        log.debug("Collection size before {}", serviceNameCollection.getIntervals().size());
        List<ObservedInterval> intervalls = serviceNameCollection.getIntervals();
        assertEquals(intervalls.size(), 1);
        repository.persistAndResetStatistics(SERVICE_NAME,1L);
        assertNull(repository.getCollection(SERVICE_NAME,1L));
    }

    private static List<ObservedMethod> observedMethodsStubs() {
        List<ObservedMethod> observedMethods = new ArrayList<>();
        long end = System.currentTimeMillis();
        long start = new DateTime(end).minusMillis(50).getMillis();
        observedMethods.add(new ObservedMethod(SERVICE_NAME,"firstMethod",start, end));
        return observedMethods;
    }
}
