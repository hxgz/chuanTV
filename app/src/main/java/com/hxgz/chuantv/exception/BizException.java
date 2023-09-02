package com.hxgz.chuantv.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class BizException extends RuntimeException {
    Exception sourceE;

    public BizException(String message) {
        super(message);
    }

    public static BizException of(Exception sourceE) {
        BizException e = new BizException(sourceE.getMessage());
        e.setSourceE(sourceE);
        return e;
    }
}