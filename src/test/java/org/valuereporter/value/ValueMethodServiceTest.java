package org.valuereporter.value;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.valuereporter.implemented.ImplementedMethodDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ValueMethodServiceTest {

    private ValueDao mockValueDao;
    private ImplementedMethodDao mockImplementedMethodDao;
    private ValueMethodService valueMethodService;

    @BeforeMethod
    public void setUp() throws Exception {
        mockValueDao = mock(ValueDao.class);
        mockImplementedMethodDao = mock(ImplementedMethodDao.class);
        valueMethodService = new ValueMethodService(mockImplementedMethodDao, mockValueDao);
    }




    @Test
    public void testFindValuableMethods() throws Exception {

    }

    @Test
    public void testFindValuableDistribution() throws Exception {
        String serviceName = "test";
        /** TODO put on hold
        ArrayList<ValuableMethod> implementedList = (ArrayList<ValuableMethod>) buildStubList();
        when(mockImplementedMethodDao.findImplementedMethodsByPrefix(eq(serviceName))).thenReturn(implementedList);
        List<ValuableMethod> usedList = buildUsedStubList();
        Mockito.doReturn(usedList).when(mockValueDao.findUsageByMethod(eq(serviceName)));
        valueMethodService.findValuableDistribution(serviceName);
         **/

    }

    @Test
    public void testBuildMap() throws Exception {
        List<ValuableMethod> stubList = buildStubList();
        Map<String, ValuableMethod> valuableMethodMap= valueMethodService.buildMap(stubList);
        assertNotNull(valuableMethodMap);
        assertEquals(valuableMethodMap.size(), stubList.size());
        assertEquals(valuableMethodMap.get("methodInUse").getName(), "methodInUse");


    }

    protected List<ValuableMethod> buildStubList() {
        List<ValuableMethod> valuableMethods = new ArrayList<>();
        valuableMethods.add(new ValuableMethod("methodInUse", 1));
        valuableMethods.add(new ValuableMethod("methodInUse2", 2));
        valuableMethods.add(new ValuableMethod("methodInUse5", 5));
        valuableMethods.add(new ValuableMethod("methodInUse10", 10));
        valuableMethods.add(new ValuableMethod("unusedMethod", 0));
        return valuableMethods;
    }

    protected List<ValuableMethod> buildUsedStubList() {
        List<ValuableMethod> valuableMethods = new ArrayList<>();
        valuableMethods.add(new ValuableMethod("methodInUse", 1));
        valuableMethods.add(new ValuableMethod("methodInUse2", 2));
        valuableMethods.add(new ValuableMethod("methodInUse5", 5));
        valuableMethods.add(new ValuableMethod("methodInUse10", 10));
        return valuableMethods;
    }
}