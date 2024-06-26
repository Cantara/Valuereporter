package org.valuereporter.scheduler;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.valuereporter.observation.ObservationsRepository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StatisticsPersisterTest {

    private ObservationsRepository repository;

    @BeforeMethod
    public void setUp() throws Exception {
        repository = mock(ObservationsRepository.class);

    }

    @Test
    public void testStartScheduler() throws Exception {
        StatisticsPersister persister = new StatisticsPersister(1,1,3);
        persister.startScheduler(repository, "testprefix");
        Thread.sleep(1500);
        verify(repository).persistAndResetStatistics(eq("testprefix"), eq(1L));

    }


}