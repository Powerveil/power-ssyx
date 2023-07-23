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

    @ExceptionHandler(Exception.class) // 异常处理器
    public Result fail(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }

    // 自定义异常处理类
    @ExceptionHandler(SsyxException.class)
    public Result fail2(Exception e) {
        return Result.fail(null);
    }
}
