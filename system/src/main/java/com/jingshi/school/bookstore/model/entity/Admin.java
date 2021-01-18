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

import java.util.Date;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@Data
@TableName("mall_admin")
public class Admin {
    @TableId(value="id",type= IdType.AUTO)
    private long id;

    @TableField(value="user_name")
    private String userName;

    @TableField(value="password")
    private String password;

    @TableField(value="is_locked")
    private boolean isLocked;

    @TableField(value="create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    @TableField(value="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;

}