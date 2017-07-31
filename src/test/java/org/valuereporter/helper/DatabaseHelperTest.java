package org.valuereporter.helper;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class DatabaseHelperTest {

    public static void main(String[] args) throws Exception {
        EmbeddedDatabaseHelper databaseHelper = new EmbeddedDatabaseHelper();
        databaseHelper.initializeDatabase();
    }
}
