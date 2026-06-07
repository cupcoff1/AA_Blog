package com.javaee.blog.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 * 标题转 slug 工具，中文转拼音，空格和符号转连字符
 */
public class SlugUtil {

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String toSlug(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.trim().toCharArray()) {
            if (Character.isWhitespace(c) || c == '-') {
                sb.append('-');
            } else if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else if (c > 127) {
                try {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyin != null && pinyin.length > 0) {
                        sb.append(pinyin[0]);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return sb.toString()
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "").toLowerCase();
    }
}
