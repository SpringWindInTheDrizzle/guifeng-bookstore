/**
 * FileName: GoodsOrderByEnum
 * Author:   sky
 * Date:     2020/1/3 15:17
 * Description:
 */
package com.jingshi.school.bookstore.model.enums;

/**
 *
 *
 * @author sky
 * @create 2020/1/3
 * @since 1.0.0
 */
public enum GoodsOrderByEnum {

    STOCK("stockNum"),

    NEW("new"),

    PRICK_UP("prickUp"),

    PRICK_DOWN("prickDown");

    private String result;

    public String getResult() {
        return result;
    }

    GoodsOrderByEnum(String result) {
        this.result = result;
    }
}