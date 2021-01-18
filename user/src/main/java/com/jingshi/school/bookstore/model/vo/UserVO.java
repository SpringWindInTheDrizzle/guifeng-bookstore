/**
 * FileName: UserVO
 * Author:   sky
 * Date:     2020/5/4 18:51
 * Description:
 */
package com.jingshi.school.bookstore.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingshi.school.bookstore.model.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sky
 * @create 2020/5/4
 * @since 1.0.0
 */
@Data
public class UserVO implements Serializable {

    public UserVO(){

    }

    public UserVO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.avatar = user.getAvatar();
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.gmtCreate = gson.toJson(user.getGmtCreate());
        this.gmtCreate = this.gmtCreate.substring(1, this.gmtCreate.length() - 1);
    }

    private Long id;

    private String userName;

    private String avatar;

    private String nickName;

    private String phone;

    private String gmtCreate;
}