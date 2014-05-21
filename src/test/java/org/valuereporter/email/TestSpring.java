package org.valuereporter.email;

import org.valuereporter.observation.ObservationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

@Test
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class TestSpring extends AbstractTestNGSpringContextTests {



    @Autowired
    JdbcTemplate jdbcTemplate;

	@Test()
	void testEmailGenerator() {



        assertNotNull(jdbcTemplate);
        ObservationDao observationDao = new ObservationDao(jdbcTemplate);
        assertNotNull(observationDao);
        assertEquals(1, observationDao.findObservedMethods("dill", "org.valuereporter.template.welcome").size());

		

	}

}