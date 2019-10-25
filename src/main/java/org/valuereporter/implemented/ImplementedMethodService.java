package org.valuereporter.implemented;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Service
public class ImplementedMethodService implements QueryOperations, WriteOperations {
    private static final Logger log = LoggerFactory.getLogger(ImplementedMethodService.class);
    ImplementedMethodDao implementedMethodDao;

    @Autowired
    public ImplementedMethodService(ImplementedMethodDao implementedMethodDao) {
        this.implementedMethodDao = implementedMethodDao;
    }

    @Override
    public List<ImplementedMethod> findImplementedMethods(String prefix, String name) {
        List<ImplementedMethod> implementedMethods = new ArrayList<>();
        if (prefix != null && name != null) {
            implementedMethods = implementedMethodDao.findImplementedMethods(prefix,name);
        } else if (prefix != null) {
            implementedMethods = implementedMethodDao.findImplementedMethodsByPrefix(prefix);
        }
        return implementedMethods;
    }

    @Override
    public long addImplementedMethods(List<ImplementedMethodJson> implementedMethods) {
        long size = 0;
        if (implementedMethods != null) {
            size = implementedMethodDao.addAll(implementedMethods);
        }
        return size;
    }

    @Override
    public List<String> findImplementedPrefixes() {
        return implementedMethodDao.findImplementedPrefixes();
    }
}
