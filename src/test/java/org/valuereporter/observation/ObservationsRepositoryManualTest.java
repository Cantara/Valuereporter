package org.valuereporter.observation;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
@ContextConfiguration(locations = {"classpath:applicationContext-manual-test.xml"})
public class ObservationsRepositoryManualTest extends AbstractTestNGSpringContextTests {
    private static final Logger log = LoggerFactory.getLogger(ObservationsRepositoryManualTest.class);
    private final ObservationsRepository repository;
    private final ObservationDao observationDao;
    private final static String SERVICE_NAME = "ManualTest";

    @Autowired
    public ObservationsRepositoryManualTest(ObservationsRepository repository, ObservationDao observationDao) {
        this.repository = repository;
        this.observationDao = observationDao;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/applicationContext-manual-test.xml");

        log.info("Spring context initialized.");

        JdbcTemplate jdbcTemplate = (JdbcTemplate) applicationContext.getBean("jdbcTemplate");
        int keysBefore = count(jdbcTemplate, "ObservedKeys");
        int intervalsBefore = count(jdbcTemplate, "ObservedInterval");
        ObservationDao dao = new ObservationDao(jdbcTemplate);
        ObservationsRepository repo = new ObservationsRepository(dao);
        ObservationsRepositoryManualTest test = new ObservationsRepositoryManualTest(repo, dao);
        test.testObservationsRepositoryAddAndPersist();
        int keysAfter = count(jdbcTemplate, "ObservedKeys");
        int intervalsAfter = count(jdbcTemplate, "ObservedInterval");
        log.info("Done testing. \n  ObservedKeys before {}, after {} \n  ObservedIntervals before {}, after {}",
                keysBefore, keysAfter, intervalsBefore, intervalsAfter);

    }

    private static Integer count(JdbcTemplate jdbcTemplate, String table) {
        return jdbcTemplate.queryForObject("select count(*) from " + table, Integer.class);
    }


    public void testObservationsRepositoryAddAndPersist() {
        repository.updateStatistics(SERVICE_NAME,1L,observedMethodsStubs());
        PrefixCollection serviceNameCollection = repository.getCollection(SERVICE_NAME, 1L);
        List<ObservedInterval> intervalls = serviceNameCollection.getIntervals();
        repository.persistAndResetStatistics(SERVICE_NAME, 1L);
    }

    private static List<ObservedMethod> observedMethodsStubs() {
        List<ObservedMethod> observedMethods = new ArrayList<>();
        long end = System.currentTimeMillis();
        long start = new DateTime(end).minusMillis(50).getMillis();
        observedMethods.add(new ObservedMethod(SERVICE_NAME,"firstMethod",start, end));
        observedMethods.add(new ObservedMethod(SERVICE_NAME,"firstMethod",start +2, end +3));
        observedMethods.add(new ObservedMethod(SERVICE_NAME,"secondMethodddd",start +10, end +12));
        return observedMethods;
    }
}
