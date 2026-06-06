package com.javaee.blog.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 参数校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(ResultCode.BAD_REQUEST, msg);
    }

    /** 其他未捕获异常 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.fail(ResultCode.SERVER_ERROR, e.getMessage());
    }
}
