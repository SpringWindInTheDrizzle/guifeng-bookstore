/**
 * FileName: ScheduledTask
 * Author:   sky
 * Date:     2020/5/7 00:28
 * Description:
 */
package com.jingshi.school.bookstore.util;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.google.gson.Gson;
import com.jingshi.school.bookstore.dao.SeckillProductMapper;
import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author sky
 * @create 2020/5/7
 * @since 1.0.0
 */
@DS("mysql")
@Component
public class ScheduledTask {

    Gson gson = new Gson();

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    SeckillProductMapper seckillProductMapper;

    // 30秒执行一次
    @Scheduled(fixedRate = 30000)
    public void scheduledTask() {
        // 确保超时支付订单能正常回退到redis中
        Set<Object> set = redisTemplate.opsForZSet().rangeByScore("endSeckill", 0, System.currentTimeMillis());
        if (!set.isEmpty()) {
            for (Object object : set) {
                SeckillProduct product = gson.fromJson(redisTemplate.opsForHash().get("seckill", "seckill:" + object).toString(), SeckillProduct.class);
                String num = String.valueOf(redisTemplate.opsForValue().get(object.toString()));
                product.setStockNum(Integer.valueOf(num));
                seckillProductMapper.updateById(product);
                // 清空redis
                redisTemplate.opsForHash().delete("seckill", "seckill:" + object);
                redisTemplate.delete(String.valueOf(object.toString()));
            }
            redisTemplate.opsForZSet().removeRangeByScore("endSeckill", 0, System.currentTimeMillis());
        }
    }
}