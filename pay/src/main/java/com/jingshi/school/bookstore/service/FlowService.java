/**
 * FileName: FlowService
 * Author:   sky
 * Date:     2020/4/27 14:36
 * Description:
 */
package com.jingshi.school.bookstore.service;

import com.jingshi.school.bookstore.model.entity.Flow;

/**
 *
 *
 * @author sky
 * @create 2020/4/27
 * @since 1.0.0
 */
public interface FlowService {

    public int insert(Flow record);

    public Flow selectByPrimaryKey(String id);

}