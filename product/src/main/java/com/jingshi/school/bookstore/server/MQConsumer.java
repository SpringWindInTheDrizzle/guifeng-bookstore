/**
 * FileName: MQConsumer
 * Author:   sky
 * Date:     2020/5/8 21:51
 * Description:
 */
package com.jingshi.school.bookstore.server;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.tools.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 *
 * @author sky
 * @create 2020/5/8
 * @since 1.0.0
 */
@Component
public class MQConsumer {
    private static final Logger log = LoggerFactory.getLogger(MQConsumer.class);

    /**
     * FANOUT广播队列监听一.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception  这里异常需要处理
     */
    @RabbitListener(queues = {"FANOUT_QUEUE_A"})
    public void on(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        String responsJson = new String(message.getBody());
        log.debug("consumer FANOUT_QUEUE_B : " + responsJson);
        if (StringUtils.isNotBlank(responsJson)) {
            try {
                WebSocketServer.BroadCastInfo(responsJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
