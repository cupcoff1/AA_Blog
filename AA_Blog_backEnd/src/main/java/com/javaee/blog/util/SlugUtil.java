package com.javaee.blog.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 标题转 slug 工具，中文转拼音，空格和符号转连字符
 */
public class SlugUtil {

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /** 生成带随机后缀的 slug，避免并发竞态 */
    public static String toUniqueSlug(String input) {
        String base = toSlug(input);
        int suffix = ThreadLocalRandom.current().nextInt(0x1000, 0x10000);
        return base + "-" + Integer.toHexString(suffix);
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
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 拼音转换失败，跳过该字符
                }
            }
        }
        return sb.toString()
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "").toLowerCase();
    }
}
