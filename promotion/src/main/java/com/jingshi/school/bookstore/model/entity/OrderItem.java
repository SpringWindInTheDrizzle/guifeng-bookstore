/**
 * FileName: OrderItem
 * Author:   sky
 * Date:     2020/4/10 16:31
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
 * @create 2020/4/10
 * @since 1.0.0
 */
@Data
@TableName("mall_order_item")
public class OrderItem implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "order_no")
    private String orderNo;

    @TableField(value = "product_id")
    private Integer productId;

    @TableField(value = "product_name")
    private String productName;

    @TableField(value = "product_image")
    private String productImage;

    @TableField(value = "current_util_price")
    private Double currentUtilPrice;

    @TableField(value = "quantity")
    private Integer quantity;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}