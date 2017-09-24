package org.valuereporter.value;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.valuereporter.implemented.ImplementedMethod;
import org.valuereporter.implemented.ImplementedMethodDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Service
public class ValueMethodService implements QueryOperations, WriteOperations{
    private static final Logger log = LoggerFactory.getLogger(ValueMethodService.class);
    private static final long UNUSED = 0;

    private ImplementedMethodDao implementedMethodDao;
    private ValueDao valueDao;

    @Autowired
    public ValueMethodService(ImplementedMethodDao implementedMethodDao, ValueDao valueDao) {
        this.implementedMethodDao = implementedMethodDao;
        this.valueDao = valueDao;
    }

    @Override
    public List<ValuableMethod> findValuableMethods(String serviceName) {
        return buildStubList();
    }

    @Override
    public List<ValuableMethod> findValuableDistribution(String serviceName, String filterOnName) {
        boolean doFilter = filterOnName != null;
        ValuableMethod unused = new ValuableMethod("Unused", 0);
        ValuableMethod lowUsage = new ValuableMethod("Less than 5", 0);
        ValuableMethod highUsage = new ValuableMethod("HighUsage", 0);

        List<ImplementedMethod> implementedMethods = implementedMethodDao.findImplementedMethodsByPrefix(serviceName);
        List<ValuableMethod> usageCountMethods = valueDao.findUsageByMethod(serviceName);
        log.trace("Usage count: {}", buildUsageCountCsv(usageCountMethods));
        Map<String, ValuableMethod> usageCountMap = buildMap(usageCountMethods);

        ValuableMethod usedMethod = null;
        List<ValuableMethod> allMethods = new ArrayList<>();
        for (ImplementedMethod implementedMethod : implementedMethods) {
            usedMethod = usageCountMap.get(implementedMethod.getName());
            if (usedMethod == null) {
                usedMethod = new ValuableMethod(implementedMethod.getPrefix(), implementedMethod.getName(), UNUSED);
            }

            if (log.isTraceEnabled()) {
                allMethods.add(usedMethod);
            }

            if (doFilter && usedMethod.getName().startsWith(filterOnName)) {
                long usageCount = usedMethod.getUsageCount();
                if (usageCount < 1) {
                    unused.addUsageCount(1);
                } else if (usageCount < 5) {
                    lowUsage.addUsageCount(1);
                } else {
                    highUsage.addUsageCount(1);
                }
            } else if (!doFilter) {
                long usageCount = usedMethod.getUsageCount();
                if (usageCount < 1) {
                    unused.addUsageCount(1);
                } else if (usageCount < 5) {
                    lowUsage.addUsageCount(1);
                } else {
                    highUsage.addUsageCount(1);
                }

            }
        }

        log.trace("Implemented count: {}", buildUsageCountCsv(allMethods));

        List<ValuableMethod> valuableDistribution = new ArrayList<>(3);
        valuableDistribution.add(unused);
        valuableDistribution.add(lowUsage);
        valuableDistribution.add(highUsage);
        return valuableDistribution;
    }

    private String buildUsageCountCsv(List<ValuableMethod> usageCountMethods) {
        String usageCount = "Start.";
        for (ValuableMethod usageCountMethod : usageCountMethods) {
            usageCount = usageCount + "\n" +usageCountMethod.toCSV();
        }
        return usageCount;
    }

    protected Map<String, ValuableMethod> buildMap(List<ValuableMethod> usageCountMethods) {
        Map<String, ValuableMethod> usageCountMap = new HashMap<>();
        for (ValuableMethod usageCountMethod : usageCountMethods) {
              usageCountMap.put(usageCountMethod.getName(), usageCountMethod);
        }
        return usageCountMap;
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
}
