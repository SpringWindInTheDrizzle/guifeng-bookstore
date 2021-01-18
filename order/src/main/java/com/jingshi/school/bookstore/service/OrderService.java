package com.jingshi.school.bookstore.service;



import com.jingshi.school.bookstore.model.entity.Order;
import com.jingshi.school.bookstore.model.entity.OrderItem;

import java.util.List;

public interface OrderService {
    /**
     * 后台分页
     *
     * @return
     */
    List<Order> getOrders();


    String updateOrderInfo(Order order);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @return
     */
    String saveOrder(Order order);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    Order getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    // Order getOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param
     * @return
     */
    List<Order> getMyOrders(Integer userid);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param id
     * @return
     */
    String cancelOrder(String orderNo, Integer id);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Integer userId);

    String paySuccess(String orderNo, Byte payType);

    List<OrderItem> getOrderItems(String orderid);

    /**
     * search order by status
     * earchOrdersByStatus
     */
    List<Order> searchOrdersByStatus(Byte status);

    /**
     * user get orders by status
     */
    List<Order> getMyOrdersByStatus(Integer userid, Byte status);

    List<Order> getMyOrdersList(String userid);

    String updateOrderInfo(String orderNo, Byte orderStatus);

    void saveOrderItem(List<OrderItem> orderItem);

    String deleteOrder(String orderNo, Integer id);

    List<Order> listWaitOrder();
}
