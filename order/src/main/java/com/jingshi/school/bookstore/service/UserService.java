/**
 * FileName: UserService
 * Author:   sky
 * Date:     2020/4/3 14:21
 * Description:
 */
package com.jingshi.school.bookstore.service;



import com.jingshi.school.bookstore.model.entity.User;

import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/3
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 用户登录
     * 验证码由前面验证
     *
     * type为1表示手机登录，为0表示用户名登录
     * @param loginName
     * @param type
     * @param password
     * @return
     */
    User login(String loginName, String password, int type);

    User login(String phone); // TODO: 2020/4/9

    /**
     * 注册
     *
     * @param user
     * @return
     */
    String register(User user);

    /**
     *
     *
     * @param user
     */
    // String save(User user);

    /**
     *
     * @param user
     * @return
     */
    String updateInfo(User user);

    /**
     *
     * @param userId
     */
    String deleteUser(Integer userId);

    /**
     *
     * @param userId
     * @return
     */
    User searchUserById(Integer userId);

    List<User> listUsers();
}