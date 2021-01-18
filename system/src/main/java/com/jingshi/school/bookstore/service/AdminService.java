/**
 * FileName: AdminService
 * Author:   sky
 * Date:     2020/4/9 17:45
 * Description:
 */
package com.jingshi.school.bookstore.service;

import com.jingshi.school.bookstore.model.entity.Admin;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
public interface AdminService {
    /**
     * 管理员登录
     * 管理员以管理员用户名和密码登录，验证码为图文验证码由controller层验证
     * 此方法返回对应的管理员信息
     *
     * @param userName 管理员用户名
     * @param password 管理员密码
     * @return 查询出来的管理员，若不存在则返回为空
     */
    Admin login(String userName, String password);

    /**
     * 获取用户信息
     * 管理员可以通过管理员Id 获取管理员信息
     *
     * @param loginUserId 管理员id
     * @return 查询出来的管理员，若不存在则返回为空
     */
    Admin getUserDetailById(Integer loginUserId);

    /**
     * 修改当前登录用户的密码
     * 若原密码与数据库中不同，则返回失败
     * 管理员id在controller层从session中获取
     *
     * @param loginUserId 管理员id
     * @param originalPassword 原密码
     * @param newPassword 新密码
     * @return 若密码错误或id不存在等返回 error，否则返回 success
     */
    String updatePassword(Integer loginUserId, String originalPassword, String newPassword);

    /**
     * 修改当前登录用户的名称信息
     * 管理员id在controller层从session中获取
     *
     * @param loginUserId 管理员id
     * @param loginUserName 管理员新用户名
     * @return 查不到管理员id或数据库操作失败则返回 error，success
     */
    String updateName(Integer loginUserId, String loginUserName);


}