/**
 * FileName: ProcessReceiver
 * Author:   sky
 * Date:     2020/5/8 16:54
 * Description:
 */
package com.jingshi.school.bookstore.config;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jingshi.school.bookstore.dao.SeckillProductMapper;
import com.jingshi.school.bookstore.model.entity.Order;
import com.jingshi.school.bookstore.model.entity.OrderItem;
import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import com.jingshi.school.bookstore.service.OrderService;
import com.rabbitmq.client.Channel;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 *
 *
 * @author sky
 * @create 2020/5/8
 * @since 1.0.0
 */
@DS("mysql")
@Component
public class ProcessReceiver {

    @Resource
    SeckillProductMapper seckillProductMapper;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Reference(version = "1.0.0")
    OrderService orderService;

    private static Logger logger = LoggerFactory.getLogger(ProcessReceiver.class);

    public static final String FAIL_MESSAGE = "This message will fail";

    @RabbitListener(queues = {"delay_process_queue"})
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            processMessage(message);
        }
        catch (Exception e) {
            // 如果发生了异常，则将该消息重定向到缓冲队列，会在一定延迟之后自动重做
            channel.basicPublish(QueueConfig.PER_QUEUE_TTL_EXCHANGE_NAME, QueueConfig.DELAY_QUEUE_PER_QUEUE_TTL_NAME, null,
                    "The failed message will auto retry after a certain delay".getBytes());
        }

    }

    /**
     * 模拟消息处理。如果当消息内容为FAIL_MESSAGE的话，则需要抛出异常
     *
     * @param message
     * @throws Exception
     */

    public void processMessage(Message message) throws Exception {
        String realMessage = new String(message.getBody());
        logger.info("Received <" + realMessage + ">");

        Order order = orderService.getOrderDetailByOrderNo(realMessage, null);
        if (order == null) {
            return ;
        }
        if (order.getStatus() == 0) {
            List<OrderItem> list = orderService.getOrderItems(order.getOrderNo());
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
            order.setStatus((byte) 5);
            order.setCloseTime(new Date());
            orderService.updateOrderInfo(order);
            logger.info("超时订单： <" + realMessage + ">");
            // 将订单中的item库存返回原库存
        }

        if (Objects.equals(realMessage, FAIL_MESSAGE)) {
            throw new Exception("Some exception happened");
        }
    }
}