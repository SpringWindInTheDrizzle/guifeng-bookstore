/**
 * FileName: ProductAdminVO
 * Author:   sky
 * Date:     2020/4/30 19:22
 * Description:
 */
package com.jingshi.school.bookstore.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.io.Serializable;

/**
 *
 *
 * @author sky
 * @create 2020/4/30
 * @since 1.0.0
 */
@Data
public class ProductAdminVO implements Serializable {

    private Long id;

    private String name;

    private String author;

    private String categoryName;

    private Double originalPrice;

    private Double sellingPrice;

    private Integer stockNum;

    private Byte goodsSellStatus;
}