package org.valuereporter;

/**
 * Created with IntelliJ IDEA.
 * User: bardl
 * Date: 20.03.13
 * Time: 10:54
 */
public class ValuereporterInputException extends ValuereporterException {

    public ValuereporterInputException(String msg, StatusType statusType) {
        super(msg, statusType);
    }

    public ValuereporterInputException(String msg, Exception e, StatusType statusType) {
        super(msg, e, statusType);
    }
}
