package com.javaee.blog.common;

/** 全项目共享常量 */
public final class AppConstants {

    private AppConstants() { /* 工具类，禁止实例化 */ }

    public static final String API_PREFIX = "/api/";
    public static final String UPLOADS_PREFIX = "/uploads/";
    public static final String SPA_INDEX = "/index.html";

    public static final String ADMIN_COOKIE = "admin_token";
    public static final String COMMENTER_COOKIE = "commenter_token";

    public static final String USERNAME_ATTR = "username";

    // GitHub OAuth
    public static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    public static final String GITHUB_USER_URL = "https://api.github.com/user";
    public static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    public static final String GITHUB_SCOPE = "user:email";
}
