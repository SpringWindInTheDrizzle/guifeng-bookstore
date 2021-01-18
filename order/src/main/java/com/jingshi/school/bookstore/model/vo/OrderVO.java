/**
 * FileName: OrderVO
 * Author:   sky
 * Date:     2020/4/25 16:02
 * Description:
 */
package com.jingshi.school.bookstore.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jingshi.school.bookstore.model.entity.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/25
 * @since 1.0.0
 */
@Data
public class OrderVO implements Serializable {

    private Integer id;

    private String orderNo;

    private Double payment;

    private Integer postage;

    private Byte status;

    private List<OrderItem> orderItems;

    private String address;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    private String updateTime;

    private Byte type;


}
