/**
 * FileName: User
 * Author:   sky
 * Date:     2020/4/3 14:19
 * Description:
 */
package com.jingshi.school.bookstore.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 *
 *
 * @author sky
 * @create 2020/4/3
 * @since 1.0.0
 */
@Data
@TableName("mall_product_info")
public class Product implements Serializable {
    @TableId(value="id",type= IdType.AUTO)
    private Long id;

    @TableField(value="name")
    private String name;

    @TableField(value="subtitle")
    private String subtitle;

    @TableField(value="category_id")
    private Integer categoryId;

    @TableField(value="main_image")
    private String mainImage;

    @TableField(value="original_price")
    private Double originalPrice;

    @TableField(value="selling_price")
    private Double sellingPrice;

    @TableField(value="stock_num")
    private Integer stockNum;

    @TableField(value="goods_sell_status")
    private Byte goodsSellStatus;

    @TableField(value="author")
    private String author;

    @TableField(value="press")
    private String press;

    @TableField(value="isbn")
    private String isbn;

    @TableField(value="create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    @TableField(value="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;

    @TableField(value="publication_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publicationTime;

}
