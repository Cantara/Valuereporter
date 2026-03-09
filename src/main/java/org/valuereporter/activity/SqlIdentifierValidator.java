package org.valuereporter.activity;

/**
 * Validates SQL identifiers (table names, column names) against a strict
 * whitelist pattern to prevent SQL injection via identifier concatenation.
 *
 * <p>PreparedStatement parameters protect values, but table and column names
 * cannot be parameterised — they must be validated before being concatenated
 * into SQL strings.
 *
 * <p>Valid identifiers: start with a letter, contain only letters, digits, and
 * underscores. Max 64 characters (MySQL/MariaDB limit).
 */
class SqlIdentifierValidator {

    private static final java.util.regex.Pattern VALID_IDENTIFIER =
            java.util.regex.Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{0,63}$");

    private SqlIdentifierValidator() {}

    /**
     * Validates that {@code identifier} is a safe SQL identifier.
     *
     * @param identifier the table or column name to validate
     * @param context    describes the identifier in error messages (e.g. "tableName")
     * @throws IllegalArgumentException if {@code identifier} is null, blank, or
     *                                  contains characters outside [a-zA-Z0-9_]
     */
    static void validate(String identifier, String context) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException(
                    "SQL identifier '" + context + "' must not be null or empty");
        }
        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException(
                    "SQL identifier '" + context + "' contains invalid characters: " + identifier);
        }
    }
}
