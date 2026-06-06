package com.javaee.blog.util;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * 标题转 slug 工具，中文转拼音，空格和符号转连字符
 */
public class SlugUtil {

    public static String toSlug(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.trim().toCharArray()) {
            if (Character.isWhitespace(c) || c == '-') {
                sb.append('-');
            } else if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else if (c > 127) {
                // 转拼音
                String py = Pinyin.toPinyin(c);
                if (py != null) {
                    sb.append(py.toLowerCase().replace(" ", "-"));
                }
            }
        }
        return sb.toString()
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "").toLowerCase();
    }
}
