package com.jingshi.order;

import com.jingshi.school.bookstore.dao.OrderMapper;
import com.jingshi.school.bookstore.model.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
class OrderApplicationTests {
@Resource
    OrderMapper orderMapper;

    @Test
    void contextLoads() {
        Order order = orderMapper.selectById(113);
        System.out.println(order.toString());
    }

}
