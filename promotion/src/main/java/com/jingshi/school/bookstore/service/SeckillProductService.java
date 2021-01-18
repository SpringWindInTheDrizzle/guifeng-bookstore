/**
 * FileName: SeckillProductService
 * Author:   sky
 * Date:     2020/4/10 19:38
 * Description:
 */
package com.jingshi.school.bookstore.service;


import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import com.jingshi.school.bookstore.util.Result;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
public interface SeckillProductService {

    /**
     * 创建秒杀图书
     * @return
     */
    String createSeckillProduct(int productId, int stockNum, Double price, Date beginTime, Date endTime);

    String deleteSeckillProduct(int seckillProductId);

    String updateSeckillProduct(int seckillProductId, Integer stockNum, Double price, Date beginTime, Date endTime);

    List<SeckillProduct> listSeckillProducts();

    SeckillProduct getSeckillProduct(Integer id);
    
    Long toOrder(Integer userId, int seckillProductId, int num);

    String getCurrentStockNum(Integer id);

    List<SeckillProduct> listProductsFromDB();

    Boolean updateStatus(SeckillProduct productId);
}