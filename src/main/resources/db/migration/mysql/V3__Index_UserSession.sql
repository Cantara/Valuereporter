-- usersession is queried by time window (StatisticsDashboard poll, cleanup jobs) and by userid + time window
-- (UserAdminWebApp UserSession log). Without these indexes every query is a full table scan.
--
-- The table the application uses is the lowercase `usersession` created by ActivitiesDao, not the
-- `userSession` from V2, and on a case-sensitive server they are different tables. On a fresh install
-- ActivitiesDao creates it after migrations have run, so this is a no-op then; existing installations
-- get the indexes here.
--
-- Large installations should build the indexes online before deploying this, e.g.
--   ALTER TABLE usersession ADD INDEX idx_usersession_starttime (starttime), ALGORITHM=INPLACE, LOCK=NONE;
-- since migrations run before the HTTP server starts. Written without CREATE INDEX IF NOT EXISTS so it
-- runs on both MySQL and MariaDB.

SET @table_exists := (SELECT COUNT(*) FROM information_schema.tables
                      WHERE table_schema = DATABASE() AND BINARY table_name = 'usersession');

SET @idx_exists := (SELECT COUNT(*) FROM information_schema.statistics
                    WHERE table_schema = DATABASE() AND BINARY table_name = 'usersession'
                      AND index_name = 'idx_usersession_starttime');
SET @ddl := IF(@table_exists = 0 OR @idx_exists > 0, 'SELECT 1',
               'CREATE INDEX idx_usersession_starttime ON usersession (starttime)');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_exists := (SELECT COUNT(*) FROM information_schema.statistics
                    WHERE table_schema = DATABASE() AND BINARY table_name = 'usersession'
                      AND index_name = 'idx_usersession_userid_starttime');
SET @ddl := IF(@table_exists = 0 OR @idx_exists > 0, 'SELECT 1',
               'CREATE INDEX idx_usersession_userid_starttime ON usersession (userid, starttime)');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
