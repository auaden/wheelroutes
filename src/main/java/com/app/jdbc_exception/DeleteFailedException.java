package com.app.jdbc_exception;

import org.springframework.dao.DataAccessException;

/**
 * Created by adenau on 16/7/16.
 */
public class DeleteFailedException extends DataAccessException {
    public DeleteFailedException(String msg) {
        super(msg);
    }
}
