/**
 * FileName: DirectReceiver
 * Author:   sky
 * Date:     2020/5/7 16:12
 * Description:
 */
package com.jingshi.school.bookstore.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingshi.school.bookstore.config.ExpirationMessagePostProcessor;
import com.jingshi.school.bookstore.config.QueueConfig;
import com.jingshi.school.bookstore.model.entity.Address;
import com.jingshi.school.bookstore.model.entity.Order;
import com.jingshi.school.bookstore.model.entity.OrderItem;
import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import com.jingshi.school.bookstore.service.AddressService;
import com.jingshi.school.bookstore.service.OrderService;
import com.jingshi.school.bookstore.service.SeckillProductService;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sky
 * @create 2020/5/7
 * @since 1.0.0
 */
@Component
public class DirectReceiver implements ChannelAwareMessageListener {

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Reference(version = "1.0.0")
    AddressService addressService;

    @Resource
    SeckillProductService seckillProductService;

    @Reference(version = "1.0.0")
    OrderService orderService;

    public void process(Map<String, Integer> testMessage) {
        Long userId = Long.valueOf(testMessage.get("userId"));
        Order order = new Order();
        order.setUserId(Math.toIntExact(userId));
        List<Address> address = addressService.getAddress(Math.toIntExact(userId));
        Address address1 = address.get(0);
        order.setUserAddressId(Math.toIntExact(address1.getId()));
        order.setPostage(0);
        order.setType((byte) 1);
        order.setStatus((byte) 0);
        order.setPaymentType((byte) 1);
        order.setOrderNo(NumberUtil.genOrderNo());
        Double num = 0.00;
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        SeckillProduct product = seckillProductService.getSeckillProduct((Integer) testMessage.get("id"));
        // product.setStockNum(product.getStockNum() - count);
        int count = testMessage.get("count");
        num += count * product.getSellingPrice();
        item.setCurrentUtilPrice(product.getSellingPrice());
        item.setProductId(Math.toIntExact(product.getId()));
        item.setProductImage(product.getMainImage());
        item.setProductName(product.getName());
        item.setQuantity(count);
        item.setOrderNo(order.getOrderNo());
        items.add(item);
        order.setPayment(num);
        orderService.saveOrder(order);
        orderService.saveOrderItem(items);
        // 5分钟内被消费，负责超时取消
        rabbitTemplate.convertAndSend(QueueConfig.DELAY_QUEUE_PER_MESSAGE_TTL_NAME,
                (Object) order.getOrderNo(), new ExpirationMessagePostProcessor((long) (1000 * 60 * 5)));
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        // try {
        String ss = new String(message.getBody());
        Map<String, Integer> result = new Gson().fromJson(ss, new TypeToken<Map<String, Integer>>() {
        }.getType());
        process(result);
//        }
//        catch (Exception e) {
//            // 如果发生了异常，则将该消息重定向到缓冲队列，会在一定延迟之后自动重做
//            channel.basicPublish(QueueConfig.PER_QUEUE_TTL_EXCHANGE_NAME, QueueConfig.DELAY_QUEUE_PER_QUEUE_TTL_NAME, null,
//                    "The failed message will auto retry after a certain delay".getBytes());
//        }
    }
}