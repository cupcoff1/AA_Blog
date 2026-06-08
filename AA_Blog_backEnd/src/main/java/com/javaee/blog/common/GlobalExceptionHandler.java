package com.javaee.blog.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(ResultCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleBadRequest(IllegalArgumentException e) {
        return Result.fail(ResultCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("数据完整性约束违反", e);
        return Result.fail(ResultCode.BAD_REQUEST, "数据冲突，请检查输入");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleMalformedJson(HttpMessageNotReadableException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "请求体格式错误");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleUploadSize(MaxUploadSizeExceededException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "文件大小超过限制");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "缺少必要参数：" + e.getParameterName());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return Result.fail(ResultCode.SERVER_ERROR, "服务器内部错误");
    }
}
