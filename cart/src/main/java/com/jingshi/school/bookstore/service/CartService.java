/**
 * FileName: CartService
 * Author:   sky
 * Date:     2020/4/10 16:56
 * Description:
 */
package com.jingshi.school.bookstore.service;

import com.jingshi.school.bookstore.model.entity.Cart;

import java.util.List;

/**
 *
 *
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
public interface CartService {
    /**
     * 保存商品至购物车中
     *
     * @param
     * @return
     */
    boolean saveCartItem(Cart shoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param shoppingCartItem
     * @return
     */
    void updateCartItem(Cart shoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param shoppingCartItemId
     * @return
     */
    Cart getCartItemById(Long shoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param shoppingCartItemId
     * @return
     */
    Boolean deleteById(Long shoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param userId
     * @return
     */
    List<Cart> getMyShoppingCartItems(Long userId);
}