package com.hxgz.chuantv.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class BizException extends Exception {
    public BizException(String message) {
        super(message);
    }

}