package org.valuereporter.observation;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class PrefixCollectionTest {

    @Test
    public void testUpdateStatistics() throws Exception {
        PrefixCollection serviceNameCollection = new PrefixCollection("test");
        ObservedMethod method = new ObservedMethod("testMethod", System.currentTimeMillis(), System.currentTimeMillis());
        serviceNameCollection.updateStatistics(method);

    }

    @Test
    public void testGetIntervalls() throws Exception {
        PrefixCollection serviceNameCollection = new PrefixCollection("test");
        ObservedMethod method1a = new ObservedMethod("testMethod", System.currentTimeMillis(), System.currentTimeMillis());
        ObservedMethod method1b = new ObservedMethod("testMethod", System.currentTimeMillis()+2, System.currentTimeMillis()+3);
        ObservedMethod method2 = new ObservedMethod("testMethod2", System.currentTimeMillis(), System.currentTimeMillis());
        serviceNameCollection.updateStatistics(method1a);
        serviceNameCollection.updateStatistics(method1b);
        serviceNameCollection.updateStatistics(method2);

        List<ObservedInterval> intervalls = serviceNameCollection.getIntervals();
        assertNotNull(intervalls);
        assertEquals(intervalls.size(),2);
        ObservedInterval testMethod = findInList("testMethod", intervalls);
        assertEquals(testMethod.getCount(),2);
        ObservedInterval testMethod2 = findInList("testMethod2", intervalls);
        assertEquals(testMethod2.getCount(),1);



    }

    private ObservedInterval findInList(String testMethod, List<ObservedInterval> intervalls) {
        for (ObservedInterval intervall : intervalls) {
            if (intervall.getMethodName().equals(testMethod))
                return intervall;
        }
        return null;
    }
}