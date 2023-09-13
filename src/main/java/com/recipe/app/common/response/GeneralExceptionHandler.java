package com.recipe.app.common.response;

import com.recipe.app.common.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

import static com.recipe.app.common.response.BaseResponse.*;


@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ResponseEntity<BaseResponse<?>> newResponse(BaseException e, HttpStatus status) {
        return new ResponseEntity<>(error(e), status);
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<?> handleBaseException(BaseException e) {
        log.error(e.getMessage());
        return newResponse(e, HttpStatus.OK);
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<?> handleSQLException(Exception e) {
        log.error(e.getMessage());
        return newResponse(new BaseException(BaseResponseStatus.DATABASE_ERROR), HttpStatus.OK);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return newResponse(new BaseException(BaseResponseStatus.UNKNOWN_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
