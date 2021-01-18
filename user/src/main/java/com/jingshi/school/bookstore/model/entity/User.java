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
import lombok.ToString;

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
@ToString(callSuper = true)
@TableName("mall_user")
public class User implements Serializable {
    @TableId(value="id",type= IdType.AUTO)
    private long id;

    @TableField(value="user_name")
    private String userName;

    @TableField(value="avatar")
    private String avatar;

    @TableField(value="nick_name")
    private String nickName;

    @TableField(value="phone")
    private String phone;

    @TableField(value="sex")
    private Byte sex;

    @TableField(value="password")
    private String password;

    @TableField(value="introduce_sign")
    private String introduceSign;

    @TableField(value="is_deleted")
    private Byte isDeleted;

    @TableField(value="is_locked")
    private Byte isLocked;

    @TableField(value="gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    @TableField(value="gmt_modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;

}
