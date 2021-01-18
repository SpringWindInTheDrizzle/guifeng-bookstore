/**
 * FileName: ProductDetailServiceImpl
 * Author:   sky
 * Date:     2020/4/10 15:38
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.ProductDetailMapper;
import com.jingshi.school.bookstore.model.entity.ProductDetail;
import com.jingshi.school.bookstore.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@DS("mysql")
@Service
@org.apache.dubbo.config.annotation.Service(version="1.0.0")
public class ProductDetailServiceImpl implements ProductDetailService {

    @Autowired
    ProductDetailMapper productDetailMapper;

    @Override
    @DS("slave")
    public ProductDetail getProductDetail(int productId){
        ProductDetail productDetail = productDetailMapper.selectOne(Wrappers.<ProductDetail>lambdaQuery()
                .eq(ProductDetail::getProductId, productId));
        return productDetail;
    }

    @Override
    public String updateProductDetail(int productId, String newDetail) {// TODO: 2020/4/10  
        return "TODO";
    }

    @Override
    public String deleteProductDetail(int productId) {// TODO: 2020/4/10  
        return "TODO";
    }

    @Override
    public String insertProductDetail(int productId, String detail) {// TODO: 2020/4/10  
        return "TODO";
    }
}