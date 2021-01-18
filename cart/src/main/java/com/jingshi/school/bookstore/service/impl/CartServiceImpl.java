/**
 * FileName: CartServiceImpl
 * Author:   sky
 * Date:     2020/4/10 16:58
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.CartMapper;
import com.jingshi.school.bookstore.model.entity.Cart;
import com.jingshi.school.bookstore.service.CartService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@DS("mysql")
@org.springframework.stereotype.Service
@Service(version = "1.0.0")
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper shoppingCartItemMapper;


    public CartServiceImpl() {
    }

    @Override
    public boolean saveCartItem(Cart shoppingCartItem) {
        //TODO
        Cart cart = shoppingCartItemMapper.selectOne(Wrappers.<Cart>lambdaQuery().eq(Cart::getUser_id, shoppingCartItem.getUser_id())
                .eq(Cart::getProductId, shoppingCartItem.getProductId()));
        if (cart == null) {
            int count = shoppingCartItemMapper.insert(shoppingCartItem);
            if (count == 1) {
                return true;
            }
            return false;
        } else {
            cart.setQuantity(cart.getQuantity() + shoppingCartItem.getQuantity());
            int count = shoppingCartItemMapper.updateById(cart);
            if (count == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void updateCartItem(Cart shoppingCartItem) {
        shoppingCartItem.setUpdateTime(new Date());
        shoppingCartItemMapper.updateById(shoppingCartItem);
    }

    @Override
    @DS("slave")
    public Cart getCartItemById(Long shoppingCartItemId) {
        Cart item = shoppingCartItemMapper.selectOne(
                new QueryWrapper<Cart>()
                        .eq("id", shoppingCartItemId)
        );
        return item;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId) {
        int row = shoppingCartItemMapper.deleteById(shoppingCartItemId);
        if (row == 1)
            return true;
        return false;
    }

    @Override
    @DS("slave")
    public List<Cart> getMyShoppingCartItems(Long userId) {
        List<Cart> items = shoppingCartItemMapper.selectList(Wrappers.<Cart>lambdaQuery().eq(Cart::getUser_id, userId));
        return items;
    }

//    private List<Cart> returnShoppingCartDTO(List<Cart> items) {
//        Cart shoppingCartItemVO = new Cart();
//        String goodsName="";
//        String goodsCoverImg = "";
//        String goodsIntro="";
//        double price = 0;
//        List<Cart> list = new ArrayList<>();
//        for (Cart shoppingCartItem:items) {
//            Cart shoppingCart = new Cart();
//            Product goodsInfo = goodsService.getMallGoodsById(shoppingCartItem.getProductId());
//            goodsName = goodsInfo.getName();
//            goodsCoverImg = goodsInfo.getMainImage();
//            price = goodsInfo.getSellingPrice();
//            goodsIntro = goodsInfo.getSubtitle();
////            shoppingCart.setCartItemId(shoppingCartItem.getId());
////            shoppingCart.setGoodsCount(shoppingCartItem.getGoodsCount());
////            shoppingCart.setGoodsCoverImg(goodsCoverImg);
////            shoppingCart.setGoodsName(goodsName);
////            shoppingCart.setSellingPrice(price);
////            shoppingCart.setGoodsDesc(goodsIntro);
////            shoppingCart.setGoodsId(shoppingCartItem.getGoodsId());
//            list.add(shoppingCart);
//        }
//        return list;
//    }

}