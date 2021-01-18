/**
 * FileName: SekcillProduct
 * Author:   sky
 * Date:     2020/4/10 19:32
 * Description:
 */
package com.jingshi.school.bookstore.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@Data
@TableName("mall_product_seckill_info")
public class SeckillProduct {

    public SeckillProduct() {

    }
    public SeckillProduct(Product product) {
        this.productId = product.getId();
        this.author = product.getAuthor();
        this.categoryId = product.getCategoryId();
        this.isbn = product.getIsbn();
        this.mainImage = product.getMainImage();
        this.name = product.getName();
        this.originalPrice = product.getOriginalPrice();
        this.press = product.getPress();
        this.subtitle = product.getSubtitle();
        this.publicationTime = product.getPublicationTime();
    }

    @TableId(value="id",type= IdType.AUTO)
    private Long id;

    @TableField(value="goods_sell_status")
    private Byte goodsSellStatus;

    @TableField(value="name")
    private String name;

    @TableId(value="product_id")
    private Long productId;

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

    @TableField(value="start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTIME;

    @TableField(value="end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
}