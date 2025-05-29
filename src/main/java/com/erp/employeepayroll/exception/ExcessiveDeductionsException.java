package com.erp.employeepayroll.exception;


public class ExcessiveDeductionsException extends RuntimeException {
    public ExcessiveDeductionsException(String message) {
        super(message);
    }
}