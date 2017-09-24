package org.valuereporter.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.valuereporter.utils.XSSFilter.hasXssRisk;

/**
 * Created by baardl on 2017-01-16.
 */
public class XSSFilterTest {
    public static final String XSS_FAULTY = "window.location=74796878/*'*(window.location=74796878)*'\"*(window.location=74796878)*\"*/";
    private static final String SAFE_CONTENT = "some_serviceNameeWeWant4Us";
    @Test
    public void testSpring4Xss() throws Exception {
        assertTrue(hasXssRisk(XSS_FAULTY));
        assertFalse(hasXssRisk(SAFE_CONTENT));
    }

}