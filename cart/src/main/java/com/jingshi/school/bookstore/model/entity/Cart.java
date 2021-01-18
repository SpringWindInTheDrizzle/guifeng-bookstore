/**
 * FileName: Cart
 * Author:   sky
 * Date:     2020/4/10 16:54
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
@TableName("mall_cart")
public class Cart implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "user_id")
    private Integer user_id;

    @TableField(value = "product_id")
    private Integer productId;

    @TableField(value = "quantity")
    private Integer quantity;

    @TableField(value = "is_checked")
    private Byte is_checked;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}