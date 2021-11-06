package com.xcion.webmage.utils;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/9/18 14:12
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/18 14:12
 * @Version: 1.0
 * @Description: Unicode 编码解决 中文
 * @UpdateRemark:
 */
public class UnicodeUtils {

    /**
     * 中文转Unicode
     *
     * @param gbString 中文
     * @return 编码
     */
    public static String gbEncoding(final String gbString) {
        if (gbString.isEmpty()) {
            return null;
        }
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }


    /**
     * Unicode转中文
     *
     * @param dataStr code码
     * @return 中文
     */
    public static String decodeUnicode(final String dataStr) {
        if (dataStr.isEmpty()) {
            return null;
        }
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

}
