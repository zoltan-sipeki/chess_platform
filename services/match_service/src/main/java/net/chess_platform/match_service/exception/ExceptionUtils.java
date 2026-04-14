package net.chess_platform.match_service.exception;

import org.hibernate.exception.ConstraintViolationException;

public class ExceptionUtils {

    public static Exception convert(ConstraintViolationException ex) {
        var constraint = ex.getConstraintName();
        if (constraint.endsWith("_fkey")) {
            return new EntityNotFoundException();
        }

        if (constraint.endsWith("_pkey") || constraint.endsWith("_key")) {
            return new EntityAlreadyExistsException();
        }

        return ex;
    }
}
