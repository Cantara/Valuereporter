package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(enabled = false)
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class ObservationDaoTest extends AbstractTestNGSpringContextTests {
    private static final Logger log = LoggerFactory.getLogger(ObservationDaoTest.class);


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test(enabled = false)
    void findObservedMethods() {


        assertNotNull(jdbcTemplate);
        ObservationDao observationDao = new ObservationDao(jdbcTemplate);
        assertNotNull(observationDao);
        List<ObservedMethod> observations = observationDao.findObservedMethods("dill", "org.valuereporter.template.welcome");
        assertNotNull(observations);
        for (ObservedMethod observation : observations) {
            log.debug(observation.toString());
        }
        assertTrue(observations.size() > 0);


    }
}
