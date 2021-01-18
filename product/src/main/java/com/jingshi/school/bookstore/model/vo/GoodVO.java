/**
 * FileName: GoodVO
 * Author:   sky
 * Date:     2020/5/4 16:33
 * Description:
 */
package com.jingshi.school.bookstore.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingshi.school.bookstore.model.entity.Product;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author sky
 * @create 2020/5/4
 * @since 1.0.0
 */
@Data
public class GoodVO implements Serializable {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public GoodVO(Product product) {
        this.id = product.getId();
        this.author = product.getAuthor();
        this.name = product.getName();
        this.categoryId = product.getCategoryId();
        this.publicationTime = gson.toJson(product.getPublicationTime());
        this.publicationTime = this.publicationTime.substring(1, 8);
        this.goodsSellStatus = product.getGoodsSellStatus();
        this.isbn = product.getIsbn();
        this.mainImage = product.getMainImage();
        this.originalPrice = product.getOriginalPrice();
        this.sellingPrice = product.getSellingPrice();
        this.stockNum = product.getStockNum();
        this.press = product.getPress();
        this.subtitle = product.getSubtitle();
    }

    private Long id;

    private String name;

    private String subtitle;

    private Integer categoryId;

    private String mainImage;

    private Double originalPrice;

    private Double sellingPrice;

    private Integer stockNum;

    private Byte goodsSellStatus;

    private String author;

    private String press;

    private String isbn;

    private String publicationTime;
}