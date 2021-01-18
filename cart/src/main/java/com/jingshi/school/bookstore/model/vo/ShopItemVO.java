/**
 * FileName: ShopItemVO
 * Author:   sky
 * Date:     2020/4/23 18:01
 * Description:
 */
package com.jingshi.school.bookstore.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 * @author sky
 * @create 2020/4/23
 * @since 1.0.0
 */
@Data
public class ShopItemVO implements Serializable {

    private Integer id;

    private Integer productId;

    private String productName;

    private String author;

    private Double price;

    private Integer quantity;

    private Byte is_checked;

    private String mainImage;

}