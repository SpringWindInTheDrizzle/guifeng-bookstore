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
@TableName("mall_order")
public class Order implements Serializable {
    @TableId(value="id",type= IdType.AUTO)
    private Integer id;

    @TableField(value="order_no")
    private String orderNo;

    @TableField(value="user_id")
    private Integer userId;

    @TableField(value="user_address_id")
    private Integer userAddressId;

    @TableField(value="payment")
    private Double payment;

    @TableField(value="postage")
    private Integer postage;

    @TableField(value="payment_type")
    private Byte paymentType;

    @TableField(value="status")
    private Byte status;

    @TableField(value="type")
    private Byte type;

    @TableField(value="payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;

    @TableField(value="send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    @TableField(value="end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @TableField(value="close_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closeTime;

    @TableField(value="create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
