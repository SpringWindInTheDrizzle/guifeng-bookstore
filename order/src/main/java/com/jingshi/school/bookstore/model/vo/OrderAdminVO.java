/**
 * FileName: OrderAdminVO
 * Author:   sky
 * Date:     2020/5/1 13:43
 * Description:
 */
package com.jingshi.school.bookstore.model.vo;

import lombok.Data;

/**
 *
 *
 * @author sky
 * @create 2020/5/1
 * @since 1.0.0
 */
@Data
public class OrderAdminVO {

    private Integer id;

    private String orderNo;

    private String userName;

    private Double total;

    private String address;

    private String status;

}