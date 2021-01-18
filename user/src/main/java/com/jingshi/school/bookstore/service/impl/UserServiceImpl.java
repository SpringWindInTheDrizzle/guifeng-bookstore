/**
 * FileName: UserServiceImpl
 * Author:   sky
 * Date:     2020/4/3 14:27
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.UserMapper;
import com.jingshi.school.bookstore.model.entity.User;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.service.UserService;
import com.jingshi.school.bookstore.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sky
 * @create 2020/4/3
 * @since 1.0.0
 */
@DS("mysql")
@Service
@org.apache.dubbo.config.annotation.Service(version="1.0.0")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @DS("slave")
    @Override
    public User login(String loginName, String password, int type) {
        password = new MD5Util().digest(password);
        if (type == 1) {
            return userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getPhone, loginName)
                    .eq(User::getPassword, password));
        } else {
            return userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getUserName, loginName)
                    .eq(User::getPassword, password));
        }
    }

    @DS("slave")
    @Override
    public User login(String phone) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone));
    }

    @Override
    public String register(User user) {
        String password = user.getPassword();
        password = new MD5Util().digest(password);
        if (userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName,user.getUserName())) != null
               || userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone,user.getPhone())) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        user.setPassword(password);
        try {
            if (save(user).equals(ServiceResultEnum.SUCCESS.getResult())) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServiceResultEnum.OPERATE_ERROR.getResult();
    }

    private String save(User user) {
        if (userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName())) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        if (userMapper.insert(user) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateInfo(User user) {
        User temp = userMapper.selectById(user.getId());
        if (temp != null) {
            user.setGmtModified(new Date());
            if (userMapper.updateById(user) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String deleteUser(Integer userId) {
        if (userMapper.deleteById(userId) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @DS("slave")
    @Override
    public User searchUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @DS("slave")
    @Override
    public List<User> listUsers() {
        return userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getIsDeleted, 0));
    }

}