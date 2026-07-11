package com.javaee.blog.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.NoSuchElementException;
import java.util.Objects;

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
        return Result.fail(ResultCode.BAD_REQUEST,
                Objects.requireNonNullElse(e.getMessage(), ResultCode.BAD_REQUEST.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("数据库约束冲突", e);
        return Result.fail(ResultCode.BAD_REQUEST, ResultCode.MSG_DATA_CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleMalformedJson(HttpMessageNotReadableException e) {
        return Result.fail(ResultCode.BAD_REQUEST, ResultCode.MSG_MALFORMED_JSON);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleUploadSize(MaxUploadSizeExceededException e) {
        return Result.fail(ResultCode.BAD_REQUEST, ResultCode.MSG_UPLOAD_TOO_LARGE);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Result<?> handleNotFound(NoSuchElementException e) {
        return Result.fail(ResultCode.NOT_FOUND,
                Objects.requireNonNullElse(e.getMessage(), ResultCode.NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "缺少必要参数：" + e.getParameterName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return Result.fail(ResultCode.BAD_REQUEST, ResultCode.MSG_METHOD_NOT_ALLOWED + "：" + e.getMethod());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return Result.fail(ResultCode.BAD_REQUEST, "参数类型错误：" + e.getName());
    }

    @ExceptionHandler(BindException.class)
    public Result<?> handleBind(BindException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(ResultCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handleNoHandler(NoHandlerFoundException e) {
        return Result.fail(ResultCode.NOT_FOUND, "接口不存在：" + e.getRequestURL());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("服务器内部错误", e);
        return Result.fail(ResultCode.SERVER_ERROR, ResultCode.MSG_INTERNAL_ERROR);
    }
}
