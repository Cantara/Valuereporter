package org.valuereporter;


/**
 * Created with IntelliJ IDEA.
 * User: bardl
 * Date: 20.03.13
 * Time: 10:54
 * To change this template use File | Settings | File Templates.
 */
public class ValuereporterUserException extends ValuereporterException {

    public ValuereporterUserException(String msg, StatusType statusType) {
        super(msg, statusType);
    }

    public ValuereporterUserException(String msg, Exception e, StatusType statusType) {
        super(msg, e, statusType);
    }
}
