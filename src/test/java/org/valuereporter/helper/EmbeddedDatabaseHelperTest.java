package org.valuereporter.helper;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by baardl on 24.09.17.
 */
public class EmbeddedDatabaseHelperTest {
    private EmbeddedDatabaseHelper embeddedDatabaseHelper;
    @BeforeMethod
    public void setUp() throws Exception {
        embeddedDatabaseHelper = new EmbeddedDatabaseHelper();
    }

    @Test
    public void testFindHSQLDbPath() throws Exception {
        String jdbcurl = "jdbc:hsqldb:file:db/hsqldb/ValueReporter";
        assertEquals("db/hsqldb", embeddedDatabaseHelper.findHSQLDbPath(jdbcurl));

    }

    public static void main(String[] args) throws Exception {
        EmbeddedDatabaseHelper databaseHelper = new EmbeddedDatabaseHelper();
        databaseHelper.initializeDatabase();
    }

}