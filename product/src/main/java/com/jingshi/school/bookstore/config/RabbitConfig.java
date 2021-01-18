/**
 * FileName: RabbitConfig
 * Author:   sky
 * Date:     2020/5/8 21:23
 * Description:
 */
package com.jingshi.school.bookstore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 *
 *
 * @author sky
 * @create 2020/5/8
 * @since 1.0.0
 */
@Configuration
public class RabbitConfig {

    @Bean
    public FanoutExchange fanoutExchange() {
        return (FanoutExchange) ExchangeBuilder.fanoutExchange("FANOUT_EXCHANGE").durable(true).build();
    }

    @Bean
    public Queue fanoutQueueA() {
        return QueueBuilder.durable("FANOUT_QUEUE_A").build();
    }

    @Bean
    public Queue fanoutQueueB() {
        return QueueBuilder.durable("FANOUT_QUEUE_B").build();
    }

    @Bean
    public Queue fanoutQueueC() {
        return QueueBuilder.durable("FANOUT_QUEUE_C").build();
    }

    @Bean
    public Binding bindingA() {
        return BindingBuilder.bind(fanoutQueueA()).to(fanoutExchange());
    }

    @Bean
    public Binding bindingB() {
        return BindingBuilder.bind(fanoutQueueB()).to(fanoutExchange());
    }

    @Bean
    public Binding bindingC() {
        return BindingBuilder.bind(fanoutQueueC()).to(fanoutExchange());
    }

}