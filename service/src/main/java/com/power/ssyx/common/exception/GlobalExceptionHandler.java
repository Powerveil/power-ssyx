package com.power.ssyx.common.exception;

import com.power.ssyx.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Powerveil
 * @Date 2023/7/23 14:06
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result fail(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }
}
