/**
 * FileName: Flow
 * Author:   sky
 * Date:     2020/4/27 14:33
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
 * @create 2020/4/27
 * @since 1.0.0
 */
@Data
@TableName("mall_flow")
public class Flow {
    @TableId(value="id",type= IdType.AUTO)
    private Integer id;

    @TableField(value="order_no")
    private String orderNo;

    @TableField(value="flow_num")
    private String flowNum;

    @TableField(value="create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}