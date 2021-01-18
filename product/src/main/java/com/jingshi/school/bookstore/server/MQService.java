/**
 * FileName: MQService
 * Author:   sky
 * Date:     2020/5/8 21:32
 * Description:
 */
package com.jingshi.school.bookstore.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 *
 *
 * @author sky
 * @create 2020/5/8
 * @since 1.0.0
 */
@Slf4j
@Service
public class MQService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试广播模式.
     *
     * @param message
     * @return the response entity
     */
    public void fanout(String message) {
        // CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("FANOUT_EXCHANGE", "", message);
    }

    /**
     * 测试Direct模式.
     *
     * @param p the p
     * @return the response entity
     */
//    public void direct(String p) {
//        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
//        rabbitTemplate.convertAndSend("DIRECT_EXCHANGE", "DIRECT_ROUTING_KEY", p, correlationData);
//    }

}
