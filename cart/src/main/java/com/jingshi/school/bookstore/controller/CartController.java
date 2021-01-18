/**
 * FileName: CartController
 * Author:   sky
 * Date:     2020/4/10 17:10
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.jingshi.school.bookstore.model.entity.Product;
import com.jingshi.school.bookstore.model.vo.ShopItemVO;
import com.jingshi.school.bookstore.service.ProductService;
import com.jingshi.school.bookstore.model.entity.Cart;
import com.jingshi.school.bookstore.service.CartService;
import com.jingshi.school.bookstore.util.ResultGenerator;
import com.jingshi.school.bookstore.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Reference(version = "1.0.0")
    private ProductService productService;

    @PutMapping("/cart")
    @ResponseBody
    public Result updateCartItemCount(@RequestBody Cart shoppingCartItem) {
        cartService.updateCartItem(shoppingCartItem);
        return ResultGenerator.genSuccessResult("success");
    }

    @PostMapping("add/item")
    @ResponseBody
    public Result addCartItem(@RequestBody Cart shoppingCartItem) {
        shoppingCartItem.setIs_checked((byte) 0);
        if (productService.getMallGoodsById(shoppingCartItem.getProductId()).getGoodsSellStatus() == 0) {
            return ResultGenerator.genFailResult("该商品已下架");
        }
        boolean flag = cartService.saveCartItem(shoppingCartItem);
        if (flag) {
            // return getShopItemNum(request, response);
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("add error");
    }

    @GetMapping("/{userid}")
    @ResponseBody
    public Result getMyShoppingCartItems(@PathVariable Long userid) {
        List<Cart> myShoppingCartItems = cartService.getMyShoppingCartItems(userid);
        Result result = ResultGenerator.genSuccessResult("success");
        result.setData(myShoppingCartItems);
        return result;
    }

    @GetMapping("/all")
    public String getItems(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        List<Cart> myShoppingCartItems = cartService.getMyShoppingCartItems(Long.valueOf(cookieMap.get("userId").getValue()));
        List<ShopItemVO> items = new ArrayList<>();
        double priceTotal = 0;
        for (Cart cart : myShoppingCartItems) {
            ShopItemVO shopItemVO = new ShopItemVO();
            shopItemVO.setId(cart.getId());
            shopItemVO.setProductId(cart.getProductId());
            shopItemVO.setQuantity(cart.getQuantity());
            shopItemVO.setIs_checked(cart.getIs_checked());
            Product product = productService.getMallGoodsById(cart.getProductId());
            shopItemVO.setMainImage(product.getMainImage());
            shopItemVO.setProductName(product.getName());
            shopItemVO.setAuthor(product.getAuthor());
            shopItemVO.setPrice(product.getSellingPrice());
            items.add(shopItemVO);
            priceTotal += shopItemVO.getPrice() * shopItemVO.getQuantity();
        }
        int itemsTotal = items.stream().mapToInt(ShopItemVO::getQuantity).sum();
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("shopItems", items);
        return "cart";
    }

    @GetMapping("/getShopItemNum")
    @ResponseBody
    public Result get(HttpServletRequest request,
                      HttpServletResponse response) {
        return getShopItemNum(request, response);
    }

    private Result getShopItemNum(HttpServletRequest request,
                                  HttpServletResponse response) {
        Map<String, Cookie> cookieMap = new HashMap<>(16);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("no userId");
        }
        Cookie cookie = new Cookie("shopNum",
                String.valueOf(cartService.getMyShoppingCartItems(
                        Long.valueOf(cookieMap.get("userId").getValue())).size()));
        cookie.setPath("/");
        cookie.setMaxAge(2 * 60 * 60);
        response.addCookie(cookie);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/clear")
    @ResponseBody
    public Result clearCarts(HttpServletRequest request,
                             HttpServletResponse response) {
        Map<String, Cookie> cookieMap = new HashMap<>(16);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("no userId");
        }
        List<Cart> carts = cartService.getMyShoppingCartItems(Long.valueOf(cookieMap.get("userId").getValue()));
        Boolean re = true;
        for (Cart cart : carts) {
            if (!cartService.deleteById(Long.valueOf(Integer.valueOf(cart.getId())))) {
                re = false;
            }
        }
        if (re) {
            Cookie cookie = new Cookie("shopNum", String.valueOf(0));
            cookie.setPath("/");
            cookie.setMaxAge(2 * 60 * 60);
            response.addCookie(cookie);
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除错误");
        }
    }

    @PostMapping("/item/add")
    @ResponseBody
    public Result addNum(@RequestParam("cartId") String id) {
        Cart cart = cartService.getCartItemById(Long.valueOf(id));
        if (cart == null) {
            return ResultGenerator.genFailResult("无该购物项");
        } else if (productService.getMallGoodsById(cart.getProductId()).getGoodsSellStatus() == 0) {
            return ResultGenerator.genFailResult("该商品已下架");
        } else {
            cart.setQuantity(cart.getQuantity() + 1);
            cartService.updateCartItem(cart);
            return ResultGenerator.genSuccessResult();
        }
    }

    @PostMapping("/item/reduce")
    @ResponseBody
    public Result reduceNum(@RequestParam("cartId") String id) {
        Cart cart = cartService.getCartItemById(Long.valueOf(id));
        if (cart == null) {
            return ResultGenerator.genFailResult("无该购物项");
        } else if (productService.getMallGoodsById(cart.getProductId()).getGoodsSellStatus() == 0) {
            return ResultGenerator.genFailResult("该商品已下架");
        } else {
            if (cart.getQuantity() == 1) {
                return ResultGenerator.genFailResult("已经为1件，不能再减");
            }
            cart.setQuantity(cart.getQuantity() - 1);
            cartService.updateCartItem(cart);
            return ResultGenerator.genSuccessResult();
        }
    }

    @PostMapping("/item/delete")
    @ResponseBody
    public Result deleteItem(@RequestParam("cartId") String id) {
        Cart cart = cartService.getCartItemById(Long.valueOf(id));
        if (cart == null) {
            return ResultGenerator.genFailResult("无该购物项");
        } else {
            cartService.deleteById((long) cart.getId());
            return ResultGenerator.genSuccessResult();
        }
    }

}