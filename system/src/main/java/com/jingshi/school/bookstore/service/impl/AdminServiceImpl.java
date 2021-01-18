/**
 * FileName: AdminServiceImpl
 * Author:   sky
 * Date:     2020/4/9 17:45
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.service.AdminService;
import com.jingshi.school.bookstore.dao.AdminMapper;
import com.jingshi.school.bookstore.model.entity.Admin;
import com.jingshi.school.bookstore.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 *
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@DS("mysql")
@Service
@org.apache.dubbo.config.annotation.Service(version="1.0.0")
public class AdminServiceImpl implements AdminService {
    @Resource
    private AdminMapper adminUserMapper;

    @DS("slave")
    @Override
    public Admin login(String userName, String password) {
        String passwordMd5 = new MD5Util().digest(password);
        return adminUserMapper.selectOne(Wrappers.<Admin>lambdaQuery()
                .eq(Admin::getUserName, userName).eq(Admin::getPassword, passwordMd5));
    }

    /**
     *
     * @param loginUserId
     * @return
     */
    @DS("slave")
    @Override
    public Admin getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectById(loginUserId);
    }

    @Override
    public String updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        Admin adminUser = adminUserMapper.selectById(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            String originalPasswordMd5 = new MD5Util().digest(originalPassword);
            String newPasswordMd5 = new MD5Util().digest(newPassword);
            //比较原密码是否正确
            if (originalPasswordMd5.equals(adminUser.getPassword())) {
                //设置新密码并修改
                adminUser.setPassword(newPasswordMd5);
                adminUser.setGmtModified(new Date());
                if (adminUserMapper.updateById(adminUser) > 0) {
                    //修改成功则返回true
                    return ServiceResultEnum.SUCCESS.getResult();
                }
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String updateName(Integer loginUserId, String loginUserName) {
        Admin adminUser = adminUserMapper.selectById(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setUserName(loginUserName);
            adminUser.setGmtModified(new Date());
            if (adminUserMapper.updateById(adminUser) > 0) {
                //修改成功则返回true
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

}