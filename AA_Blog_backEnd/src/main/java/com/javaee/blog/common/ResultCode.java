package com.javaee.blog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器错误");

    // ==== 常用错误消息，避免硬编码 ====
    public static final String MSG_DATA_CONFLICT = "数据冲突，请检查输入";
    public static final String MSG_MALFORMED_JSON = "请求体格式错误";
    public static final String MSG_UPLOAD_TOO_LARGE = "文件大小超过限制";
    public static final String MSG_METHOD_NOT_ALLOWED = "不支持的请求方法";
    public static final String MSG_INTERNAL_ERROR = "服务器内部错误";
    public static final String MSG_WRONG_PASSWORD = "旧密码错误";
    public static final String MSG_PASSWORD_SAME = "新密码不能与旧密码相同";
    public static final String MSG_GITHUB_AUTH_FAILED = "GitHub OAuth 失败";

    private final int code;
    private final String message;
}
