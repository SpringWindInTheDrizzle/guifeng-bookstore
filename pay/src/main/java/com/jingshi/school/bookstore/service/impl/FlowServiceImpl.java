/**
 * FileName: FlowServiceImpl
 * Author:   sky
 * Date:     2020/4/27 14:37
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.FlowDao;
import com.jingshi.school.bookstore.model.entity.Flow;
import com.jingshi.school.bookstore.service.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 *
 * @author sky
 * @create 2020/4/27
 * @since 1.0.0
 */
@DS("mysql")
@Service
public class FlowServiceImpl implements FlowService {

    @Resource
    FlowDao flowDao;

    @Override
    public int insert(Flow record) {
        return flowDao.insert(record);
    }

    @DS("slave")
    @Override
    public Flow selectByPrimaryKey(String orderNo) {
        return flowDao.selectOne(Wrappers.<Flow>lambdaQuery().eq(Flow::getOrderNo, orderNo));
    }
}