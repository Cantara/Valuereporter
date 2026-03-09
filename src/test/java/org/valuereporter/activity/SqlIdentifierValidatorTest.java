package org.valuereporter.activity;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for SqlIdentifierValidator.
 * No Spring context required — pure logic.
 */
public class SqlIdentifierValidatorTest {

    // --- valid identifiers ---

    @Test
    public void testSimpleTableName() {
        SqlIdentifierValidator.validate("usersession", "tableName");
    }

    @Test
    public void testMixedCase() {
        SqlIdentifierValidator.validate("UserSession", "tableName");
    }

    @Test
    public void testWithUnderscore() {
        SqlIdentifierValidator.validate("user_session_log", "tableName");
    }

    @Test
    public void testWithDigitsAfterLetter() {
        SqlIdentifierValidator.validate("logon2026", "tableName");
    }

    @Test
    public void testColumnName() {
        SqlIdentifierValidator.validate("applicationId", "columnName");
    }

    // --- injection attempts: must throw ---

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsSemicolon() {
        SqlIdentifierValidator.validate("usersession; DROP TABLE usersession; --", "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsSpaces() {
        SqlIdentifierValidator.validate("user session", "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsCommaInColumn() {
        SqlIdentifierValidator.validate("userId, 1=1; --", "columnName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsStartsWithDigit() {
        SqlIdentifierValidator.validate("1malicious", "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsNull() {
        SqlIdentifierValidator.validate(null, "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsEmpty() {
        SqlIdentifierValidator.validate("", "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsSingleQuote() {
        SqlIdentifierValidator.validate("user'session", "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsDash() {
        SqlIdentifierValidator.validate("user-session", "tableName");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectsParenthesis() {
        SqlIdentifierValidator.validate("fn()", "tableName");
    }
}
