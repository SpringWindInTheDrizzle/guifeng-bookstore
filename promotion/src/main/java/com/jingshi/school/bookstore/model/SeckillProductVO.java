/**
 * FileName: SeckillProductVO
 * Author:   sky
 * Date:     2020/5/6 16:16
 * Description:
 */
package com.jingshi.school.bookstore.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingshi.school.bookstore.dao.SeckillProductMapper;
import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sky
 * @create 2020/5/6
 * @since 1.0.0
 */
@Data
public class SeckillProductVO implements Serializable {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public SeckillProductVO() {

    }

    public SeckillProductVO(SeckillProduct product) {
        this.status = product.getGoodsSellStatus();
        this.id = product.getId();
        this.name = product.getName();
        this.productId = product.getId();
        this.subtitle = product.getSubtitle();
        this.categoryId = product.getCategoryId();
        this.mainImage = product.getMainImage();
        this.originalPrice = product.getOriginalPrice();
        this.sellingPrice = product.getSellingPrice();
        this.stockNum = product.getStockNum();
        this.author = product.getAuthor();
        this.press = product.getPress();
        this.isbn = product.getIsbn();
        this.publicationTime = gson.toJson(product.getPublicationTime());
        this.publicationTime = this.publicationTime.substring(1, this.publicationTime.length() - 1);
        this.startTIME = gson.toJson(product.getStartTIME());
        this.startTIME = this.startTIME.substring(1, this.startTIME.length() - 1);
        this.endTime = gson.toJson(product.getEndTime());
        this.endTime = this.endTime.substring(1, this.endTime.length() - 1);
    }

    // 当状态为0， 表示秒杀图书入口开启，默认为0
    // 当状态为1， 表示秒杀图书关闭，只能在为开始秒杀的图书修改
    private Byte status;

    private Long id;

    private String name;

    private Long productId;

    private String subtitle;

    private Integer categoryId;

    private String mainImage;

    private Double originalPrice;

    private Double sellingPrice;

    private Integer stockNum;

    private String author;

    private String press;

    private String isbn;

    private String publicationTime;

    private String startTIME;

    private String endTime;
}