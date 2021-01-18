/**
 * FileName: PhoneUtil
 * Author:   sky
 * Date:     2020/4/16 15:21
 * Description:
 */
package com.jingshi.school.bookstore.util;

/**
 *
 *
 * @author sky
 * @create 2020/4/16
 * @since 1.0.0
 */
public class PhoneUtil {
    static String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    public static boolean isPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(regex);
    }
}