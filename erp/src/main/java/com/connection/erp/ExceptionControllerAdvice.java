package com.connection.erp;

import com.sap.conn.jco.JCoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        exception.printStackTrace();
        JCoException jCoException = new JCoException(108, exception.getMessage());
        byte[] bodyBytes = getBody(jCoException);
        if(bodyBytes != null) {
            return new ResponseEntity<>(bodyBytes, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(value = JCoException.class)
    public ResponseEntity<Object> jcoException(JCoException exception) {
        exception.printStackTrace();
        byte[] bodyBytes = getBody(exception);
        if(bodyBytes != null) {
            return new ResponseEntity<>(bodyBytes, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] getBody(JCoException exception) {
        byte[] bodyBytes = null;
        try {
            bodyBytes = SerializationUtils.serialize(exception);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bodyBytes;
    }
}
