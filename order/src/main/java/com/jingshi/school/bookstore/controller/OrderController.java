/**
 * FileName: OrderController
 * Author:   sky
 * Date:     2020/4/10 16:45
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingshi.school.bookstore.dao.OrderMapper;
import com.jingshi.school.bookstore.model.entity.*;
import com.jingshi.school.bookstore.model.vo.OrderAdminVO;
import com.jingshi.school.bookstore.model.vo.OrderVO;
import com.jingshi.school.bookstore.service.*;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.NumberUtil;
import com.jingshi.school.bookstore.util.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/order")
public class OrderController {

    @Resource
    OrderService orderService;

    @Reference(version = "1.0.0")
    AddressService addressService;

    @Reference(version = "1.0.0")
    ProductService productService;

    @Reference(version = "1.0.0")
    CartService cartService;

    @Reference(version = "1.0.0")
    UserService userService;

    @GetMapping("/orders")
    public String getOrdersList(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        List<Order> list = orderService.getMyOrdersList(cookieMap.get("userId").getValue());

        List<OrderVO> orderVOS = new ArrayList<>();
        for (Order order : list) {
            OrderVO orderVO = new OrderVO();
            List<OrderItem> items = orderService.getOrderItems(order.getOrderNo());
            orderVO.setOrderItems(items);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            orderVO.setType(order.getType());
            orderVO.setCloseTime(gson.toJson(order.getCloseTime()));
            orderVO.setCloseTime(orderVO.getCloseTime().substring(1, orderVO.getCloseTime().length() - 1));
            orderVO.setCreateTime(gson.toJson(order.getCreateTime()));
            orderVO.setCreateTime(orderVO.getCreateTime().substring(1, orderVO.getCreateTime().length() - 1));
            orderVO.setEndTime(gson.toJson(order.getEndTime()));
            orderVO.setEndTime(orderVO.getEndTime().substring(1, orderVO.getEndTime().length() - 1));
            orderVO.setId(order.getId());
            orderVO.setOrderNo(order.getOrderNo());
            orderVO.setPayment(order.getPayment());
            orderVO.setPaymentTime(gson.toJson(order.getPaymentTime()));
            orderVO.setPaymentTime(orderVO.getPaymentTime().substring(1, orderVO.getPaymentTime().length() - 1));
            orderVO.setUpdateTime(gson.toJson(order.getUpdateTime()));
            orderVO.setUpdateTime(orderVO.getUpdateTime().substring(1, orderVO.getUpdateTime().length() - 1));
            orderVO.setSendTime(gson.toJson(order.getSendTime()));
            orderVO.setAddress(addressService.getAddressById(order.getUserAddressId()));
            orderVO.setPostage(order.getPostage());
            orderVO.setStatus(order.getStatus());
            orderVOS.add(orderVO);
        }
        orderVOS.sort((a,b)-> b.getCreateTime().compareTo(a.getCreateTime()));
        request.setAttribute("orders", orderVOS);
        return "order";
    }

    @PostMapping("/create")
    @ResponseBody
    public Result createOrder(HttpServletRequest request, HttpServletResponse response) {
        // 将购物车中的购物项放入订单项中，存入订单项和订单，修改图书库存，删除购物项
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("no userid");
        }
        Long userId = Long.valueOf(cookieMap.get("userId").getValue());
        Order order = new Order();
        order.setUserId(Math.toIntExact(userId));
        List<Address> address = addressService.getAddress(Math.toIntExact(userId));
        if (address == null || address.size() == 0) {
            return ResultGenerator.genFailResult("用户未添加地址，请添加地址");
        }
        Address address1 = address.get(0);
        order.setUserAddressId(Math.toIntExact(address1.getId()));
        order.setPostage(0);
        order.setStatus((byte) 0);
        order.setPaymentType((byte) 1);
        order.setOrderNo(NumberUtil.genOrderNo());
        Double num = 0.00;
        List<Cart> carts = cartService.getMyShoppingCartItems(userId);
        if (carts == null || carts.size() == 0) {
            return ResultGenerator.genFailResult("购物车为空");
        }
        List<OrderItem> items = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        for (Cart cart : carts) {
            OrderItem item = new OrderItem();
            Product product = productService.getMallGoodsById(cart.getProductId());
            if (product.getStockNum() < cart.getQuantity()) {
                return ResultGenerator.genFailResult(product.getName() + " 库存不足，仅剩 " + product.getStockNum() + "件");
            }
            if (product.getGoodsSellStatus() == 0) {
                return ResultGenerator.genFailResult(product.getName() + " 图书已下架，请从购物车删除此图书再尝试生成订单");
            }
            product.setStockNum(product.getStockNum() - cart.getQuantity());
            products.add(product);
            num += cart.getQuantity() * product.getSellingPrice();
            item.setCurrentUtilPrice(product.getSellingPrice());
            item.setProductId(Math.toIntExact(product.getId()));
            item.setProductImage(product.getMainImage());
            item.setProductName(product.getName());
            item.setQuantity(cart.getQuantity());
            item.setOrderNo(order.getOrderNo());
            items.add(item);
        }
        for (Cart cart : carts) {
            cartService.deleteById(Long.valueOf(cart.getId()));
        }
        for (Product product : products) {
            productService.updateMallGoods(product);
        }
        order.setPayment(num);
        orderService.saveOrder(order);
        orderService.saveOrderItem(items);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/order/cancel")
    @ResponseBody
    public Result cancelOrder(@RequestParam("orderId") Integer id) {

        orderService.cancelOrder(null, id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/order/delete")
    @ResponseBody
    public Result deleteOrder(@RequestParam("orderId") Integer id) {
        orderService.deleteOrder(null, id);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/admin/list")
    public String list(HttpServletRequest request) {
        List<Order> orders = orderService.getOrders();
        orders.sort((a,b)-> b.getCreateTime().compareTo(b.getCreateTime()));
        List<OrderAdminVO> list = new ArrayList<>();
        for (Order order : orders) {
            OrderAdminVO vo = new OrderAdminVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            User user = userService.searchUserById(order.getUserId());
            vo.setUserName(user.getUserName());
            vo.setAddress(addressService.getAddressById(order.getUserAddressId()));
            vo.setTotal(order.getPayment());
            // 0.待付款 1.待发货 2.待收货 3.交易成功 4.已取消'
            int status = order.getStatus();
            if (status == 0) {
                vo.setStatus("待付款");
            } else if (status == 1) {
                vo.setStatus("待发货");
            } else if (status == 2) {
                vo.setStatus("待收货");
            } else if (status == 3) {
                vo.setStatus("交易成功");
            } else if (status == 4){
                vo.setStatus("已取消");
            } else {
                vo.setStatus("超时取消");
            }
            list.add(vo);
        }
        request.setAttribute("orders", list);
        return "orderAdmin";
    }

    @GetMapping("/admin/wait")
    public String orderWait(HttpServletRequest request) {
        List<Order> orders = orderService.listWaitOrder();
        orders.sort((a,b)-> b.getPaymentTime().compareTo(a.getPaymentTime()));
        List<OrderAdminVO> list = new ArrayList<>();
        for (Order order : orders) {
            OrderAdminVO vo = new OrderAdminVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            User user = userService.searchUserById(order.getUserId());
            vo.setUserName(user.getUserName());
            vo.setAddress(addressService.getAddressById(order.getUserAddressId()));
            vo.setTotal(order.getPayment());
            vo.setStatus("待发货");
            list.add(vo);
        }
        request.setAttribute("orders", list);
        return "orderWait";
    }

    @PostMapping("/admin/send")
    @ResponseBody
    public Result sendOrder(@RequestParam("orderId") Integer id) {
        Order order = orderService.getOrderDetailByOrderNo(null, Long.valueOf(id));
        if (order == null) {
            return ResultGenerator.genFailResult("no this order");
        }
        if (order.getStatus() != 1) {
            return ResultGenerator.genFailResult("订单不处于待发货状态");
        }
        order.setStatus((byte) 2);
        order.setSendTime(new Date());
        orderService.updateOrderInfo(order);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/finish")
    @ResponseBody
    public Result finishOrder(@RequestParam("orderId") Integer id) {
        Order order = orderService.getOrderDetailByOrderNo(null, Long.valueOf(id));
        if (order == null) {
            return ResultGenerator.genFailResult("no this order");
        }
        if (order.getStatus() != 2) {
            return ResultGenerator.genFailResult("订单不处于已发货状态，请耐心等待");
        }
        order.setStatus((byte) 3);
        order.setEndTime(new Date());
        orderService.updateOrderInfo(order);
        return ResultGenerator.genSuccessResult();
    }


    @PostMapping("/add/order")
    @ResponseBody
    public Result toOrder(@RequestBody Cart shoppingCartItem) {
        //将购物车中的购物项放入订单项中，存入订单项和订单，修改图书库存，删除购物项
        if (shoppingCartItem.getUser_id() == null) {
            return ResultGenerator.genFailResult("no userid");
        }
        Long userId = Long.valueOf(shoppingCartItem.getUser_id());
        // cookie中获取
        // Long userId = Long.valueOf(29);
        List<Address> address = addressService.getAddress(Math.toIntExact(userId));
        if (address == null || address.size() == 0) {
            return ResultGenerator.genFailResult("用户未添加地址，请添加地址");
        }
        Order order = new Order();
        order.setUserId(Math.toIntExact(userId));
        Address address1 = address.get(0);
        order.setUserAddressId(Math.toIntExact(address1.getId()));
        order.setPostage(0);
        order.setStatus((byte) 0);
        order.setPaymentType((byte) 1);
        order.setOrderNo(NumberUtil.genOrderNo());
        Double num = 0.0;
        OrderItem item = new OrderItem();
        Product product = productService.getMallGoodsById(shoppingCartItem.getProductId());
        if (product == null) {
            return ResultGenerator.genFailResult("该图书不存在");
        }
        if (product.getGoodsSellStatus() == 0) {
            return ResultGenerator.genFailResult("该图书已下架");
        }
        if (product.getStockNum() < shoppingCartItem.getQuantity()) {
            return ResultGenerator.genFailResult(product.getName() + " 库存不足，仅剩 " + product.getStockNum() + "件");
        }
        product.setStockNum(product.getStockNum() - shoppingCartItem.getQuantity());
        num = shoppingCartItem.getQuantity() * product.getSellingPrice();
        item.setCurrentUtilPrice(product.getSellingPrice());
        item.setProductId(Math.toIntExact(product.getId()));
        item.setProductImage(product.getMainImage());
        item.setProductName(product.getName());
        item.setQuantity(shoppingCartItem.getQuantity());
        item.setOrderNo(order.getOrderNo());
        productService.updateMallGoods(product);
        order.setPayment(num);
        orderService.saveOrder(order);
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        orderService.saveOrderItem(items);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/detail/{orderNo}")
    public String detail(@PathVariable String orderNo, HttpServletRequest request) {
        List<OrderItem> items = orderService.getOrderItems(orderNo);
        request.setAttribute("orders", items);
        return "orderDetail";
    }
}