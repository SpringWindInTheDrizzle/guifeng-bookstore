/**
 * FileName: Address
 * Author:   sky
 * Date:     2020/4/9 13:50
 * Description:
 */
package com.jingshi.school.bookstore.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
@TableName("mall_user_address")
public class Address implements Serializable {
    @TableId(value="id",type= IdType.AUTO)
    private long id;

    @TableField(value="user_id")
    private Long userId;

    @TableField(value="address")
    private String address;

    @TableField(value="receiver_name")
    private String receiverName;

    @TableField(value="receiver_phone")
    private String receiverPhone;

    @TableField(value="receiver_province")
    private String receiverProvince;

    @TableField(value="receiver_city")
    private String receiverCity;

    @TableField(value="receiver_district")
    private String receiverDistrict;

    @TableField(value="create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    @TableField(value="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;

}