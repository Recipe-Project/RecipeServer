package com.recipe.app.common.response;

import com.recipe.app.common.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

import static com.recipe.app.common.response.BaseResponse.*;


@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ResponseEntity<BaseResponse<?>> newResponse(BaseResponseStatus response, HttpStatus status) {
        return new ResponseEntity<>(error(response), status);
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<?> handleBaseException(BaseException e) {
        log.error(e.getMessage());
        return newResponse(e.getStatus(), HttpStatus.OK);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage());
        return newResponse(BaseResponseStatus.UNKNOWN_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
