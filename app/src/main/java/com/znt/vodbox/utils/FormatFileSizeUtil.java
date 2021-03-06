package com.znt.vodbox.utils;

/**
 * Created by prize on 2019/3/23.
 */
/**
 * 格式化为常见文件大小形式(保留两位小数) 如4.32G,2.15M等
 *
 * @Time 2015年10月15日
 * @author lizy18
 */
public class FormatFileSizeUtil {
    /**
     * @param length
     *            文件大小(以Byte为单位)
     * @return String 格式化的常见文件大小(保留两位小数)
     */
    public static String formatFileSize(long length) {
        String result = null;
        int sub_string = 0;
        // 如果文件长度大于1GB
        if (length >= 1073741824) {
            sub_string = String.valueOf((float) length / 1073741824).indexOf(
                    ".");
            result = ((float) length / 1073741824 + "000").substring(0,
                    sub_string + 3) + "GB";
        } else if (length >= 1048576) {
            // 如果文件长度大于1MB且小于1GB,substring(int beginIndex, int endIndex)
            sub_string = String.valueOf((float) length / 1048576).indexOf(".");
            result = ((float) length / 1048576 + "000").substring(0,
                    sub_string + 3) + "MB";
        } else if (length >= 1024) {
            // 如果文件长度大于1KB且小于1MB
            sub_string = String.valueOf((float) length / 1024).indexOf(".");
            result = ((float) length / 1024 + "000").substring(0,
                    sub_string + 3) + "KB";
        } else if (length < 1024)
            result = Long.toString(length) + "B";
        return result;
    }
}