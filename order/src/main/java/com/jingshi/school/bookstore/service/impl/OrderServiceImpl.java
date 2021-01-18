/**
 * FileName: OrderServiceImpl
 * Author:   sky
 * Date:     2020/4/10 16:39
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.jingshi.school.bookstore.dao.OrderItemMapper;
import com.jingshi.school.bookstore.dao.SeckillProductMapper;
import com.jingshi.school.bookstore.model.entity.Order;
import com.jingshi.school.bookstore.model.entity.OrderItem;
import com.jingshi.school.bookstore.model.entity.Product;
import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.service.OrderService;
import com.jingshi.school.bookstore.dao.OrderMapper;
import com.jingshi.school.bookstore.service.ProductService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class OrderServiceImpl implements OrderService {

    Gson gson = new Gson();

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    SeckillProductMapper seckillProductMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Reference(version = "1.0.0")
    ProductService productService;

    // status 0,表示订单已删除
    @Override
    @DS("slave")
    public List<Order> getOrders() {
        return orderMapper.selectList(Wrappers.<Order>lambdaQuery());
    }

    @Override
    public String updateOrderInfo(Order order) {
        Order temp = orderMapper.selectById(order.getId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        } else {
            order.setUpdateTime(new Date());
            orderMapper.updateById(order);
            return ServiceResultEnum.SUCCESS.getResult();
        }
    }

    @Override
    public String updateOrderInfo(String orderNo, Byte orderStatus) {
        Order order=orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no",orderNo));
        if(order == null){
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        }
        else{
            order.setStatus(orderStatus);
            orderMapper.updateById(order);
            return ServiceResultEnum.SUCCESS.getResult();
        }
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        return null;
    }// TODO: 2020/4/10

    @Override
    public String checkOut(Long[] ids) {
        return null;
    }// TODO: 2020/4/10

    @Override
    public String closeOrder(Long[] ids) {
        return null;
    }// TODO: 2020/4/10

    @Override
    public String saveOrder(Order order) {
        orderMapper.insert(order);
        return null;
    }

    @Override
    public void saveOrderItem(List<OrderItem> orderItems){
        for (OrderItem orderItem : orderItems) {
            orderItemMapper.insert(orderItem);
        }
    }

    @Override
    public String deleteOrder(String orderNo, Integer id) {
        Order order;
        if (orderNo == null) {
            order = orderMapper.selectById(id);
        } else {
            order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery().eq(Order::getOrderNo, orderNo));
        }
        if (order == null) {
            return "null";
        }
        orderItemMapper.delete(Wrappers.<OrderItem>lambdaQuery().eq(OrderItem::getOrderNo, order.getOrderNo()));
        orderMapper.deleteById(order.getId());
        return "success";
    }

    @Override
    @DS("slave")
    public List<Order> listWaitOrder() {
        return orderMapper.selectList(Wrappers.<Order>lambdaQuery().eq(Order::getStatus, 1));
    }

    @Override
    @DS("slave")
    public Order getOrderDetailByOrderNo(String orderNo, Long userId) {
        if (orderNo == null) {
            Order order = orderMapper.selectById(userId);
            return order;
        } else {
            Order order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery().eq(Order::getOrderNo, orderNo));
            return order;
        }
    }

    // 开启数据库事务
    @Transactional(noRollbackFor = {IllegalArgumentException.class})
    @Override
    public String cancelOrder(String orderNo, Integer id) {
        Order order = null;
        if (orderNo == null) {
            order = orderMapper.selectById(id);

        } else {
            order = orderMapper.selectOne(Wrappers.<Order>lambdaQuery().eq(Order::getOrderNo, orderNo));
        }
        if (order == null) {
            return "null";
        }
        // 取消订单后，库存返回
        // 订单分为秒杀订单和普通订单，普通订单更新数据库，秒杀订单先更新redis，若以过期则更新数据库
        // 普通订单
        List<OrderItem> list = orderItemMapper.selectList(Wrappers.<OrderItem>lambdaQuery().eq(OrderItem::getOrderNo, order.getOrderNo()));
        if (order.getType() == 0) {
            for (OrderItem item : list) {
                Product product = productService.getMallGoodsById(item.getProductId());
                product.setStockNum(product.getStockNum() + item.getQuantity());
                productService.updateMallGoods(product);
            }
        } else {
            for (OrderItem item : list) {
                Object ss = redisTemplate.opsForValue().get(item.getProductId().toString());
                // 说明已经更新到数据库中
                if (ss == null) {
                    SeckillProduct seckillProduct = seckillProductMapper.selectById(item.getProductId());
                    seckillProduct.setStockNum(seckillProduct.getStockNum() + item.getQuantity());
                    seckillProductMapper.updateById(seckillProduct);
                } else {
                    redisTemplate.opsForValue().increment(item.getProductId().toString(), item.getQuantity());
                }
            }
        }
        order.setStatus((byte) 4);
        order.setCloseTime(new Date());
        orderMapper.updateById(order);
        return "success";
    }

    @Override
    public String finishOrder(String orderNo, Integer userId) {
        return null;
    }


    @Override
    @DS("slave")
    public List<Order> getMyOrders(Integer userid) {
        return  orderMapper.selectList(Wrappers.<Order>lambdaQuery().eq(Order::getUserId, userid));
    }


    /**
     * 支付成功后，调用该方法，修改支付订单支付状态
     * @param orderNo
     * @param payType
     * @return
     */
    @Override
    public String paySuccess(String orderNo, Byte payType) {
        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        map.put("payType",payType);
        // orderMapper.updatePayTypeByOrderNo(map);
        return "success";
    }

    @Override
    @DS("slave")
    public List<OrderItem> getOrderItems(String orderid) {
        List<OrderItem> orderItems = orderItemMapper.selectList(Wrappers.
                <OrderItem>lambdaQuery().eq(OrderItem::getOrderNo, orderid));
        return orderItems;
    }



    @Override
    public List<Order> getMyOrdersByStatus(Integer userid, Byte status) {
        List<Order> orderpage = orderMapper.selectList(
                new QueryWrapper<Order>()
                        .eq("order_status", status)
                        .eq("user_id",userid)
        );
        return orderpage;
    }

    @Override
    public List<Order> getMyOrdersList(String userid) {
        List<Order> list = orderMapper.selectList(Wrappers.<Order>lambdaQuery().eq(Order::getUserId, userid));
        return list;
    }

    @Override
    public List<Order> searchOrdersByStatus(Byte status) {
        List<Order> orderpage = orderMapper.selectList(
                new QueryWrapper<Order>()
                        .eq("order_status", status)
        );
        return orderpage;
    }

}