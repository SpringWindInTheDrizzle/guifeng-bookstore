/**
 * FileName: ProductDetailService
 * Author:   sky
 * Date:     2020/4/10 15:38
 * Description:
 */
package com.jingshi.school.bookstore.service;

import com.jingshi.school.bookstore.model.entity.ProductDetail;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
public interface ProductDetailService {
    ProductDetail getProductDetail(int productId);

    String updateProductDetail(int productId, String newDetail);

    String deleteProductDetail(int productId);

    String insertProductDetail(int productId, String detail);
}