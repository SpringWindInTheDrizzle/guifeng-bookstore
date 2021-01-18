/**
 * FileName: Category
 * Author:   sky
 * Date:     2020/4/9 18:24
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
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@Data
@TableName("mall_category")
public class Category implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "level")
    private Byte level;

    @TableField(value = "parent_id")
    private Integer parentId;

    @TableField(value = "name")
    private String name;

    @TableField(value = "sort_order")
    private Integer sortOrder;

    @TableField(value = "is_deleted")
    private Byte isDeleted;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}