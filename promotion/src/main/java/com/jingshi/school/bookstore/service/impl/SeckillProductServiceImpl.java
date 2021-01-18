/**
 * FileName: SeckillProductServiceImpl
 * Author:   sky
 * Date:     2020/4/10 19:44
 * Description:
 */
package com.jingshi.school.bookstore.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.jingshi.school.bookstore.model.entity.Product;
import com.jingshi.school.bookstore.model.entity.SeckillProduct;
import com.jingshi.school.bookstore.service.ProductService;
import com.jingshi.school.bookstore.service.SeckillProductService;
import com.jingshi.school.bookstore.dao.SeckillProductMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@DS("mysql")
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0")
public class SeckillProductServiceImpl implements SeckillProductService {

    Gson gson = new Gson();

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Reference(version = "1.0.0")
    ProductService productService;

    @Resource
    SeckillProductMapper seckillProductMapper;


    // 增加关联图书id
    // 计时任务，动态URL
    // 在秒杀结束后，将redis中数据更新到数据库中
    // 使用redis的zset作为时间列表
    @Override
    public String createSeckillProduct(int productId, int stockNum, Double price, Date beginTime, Date endTime) {
        // 若结束时间早于或等于开始时间，则认为该时间设置错误
        // 若beginTime比当前时间还早，则不允许创建该秒杀图书
        if (endTime.compareTo(beginTime) <= 0 || beginTime.before(new Date())) {
            return "结束时间不得早于开始时间，开始时间必须晚于当前时间";
        }
        Product product = productService.getMallGoodsById(productId);
        SeckillProduct seckillProduct = new SeckillProduct(product);
        seckillProduct.setGoodsSellStatus((byte) 0);
        seckillProduct.setStockNum(stockNum);
        seckillProduct.setSellingPrice(price);
        seckillProduct.setStartTIME(beginTime);
        seckillProduct.setEndTime(endTime);
        List<SeckillProduct> products = seckillProductMapper.selectList(Wrappers.<SeckillProduct>lambdaQuery().eq(SeckillProduct::getProductId, productId));
        SeckillProduct product1;
        if (products == null || products.isEmpty()) {
            product1 = null;
        } else {
            product1 = products.get(products.size() - 1);
        }
        if (product1 != null && !product1.getEndTime().before(new Date())) {
            return "同图书id秒杀图书最多只存在一个在售和未售秒杀图书";
        }
        if (seckillProductMapper.insert(seckillProduct) == 0) {
            return "数据库插入失败";
        } else {
            // 先存储进数据库中
            seckillProduct = seckillProductMapper.selectOne(Wrappers.<SeckillProduct>lambdaQuery().
                    eq(SeckillProduct::getProductId, productId).eq(SeckillProduct::getStartTIME, beginTime));
            // 放入redis，Map中
            redisTemplate.opsForHash().put("seckill", "seckill:" + seckillProduct.getId(), gson.toJson(seckillProduct));
            redisTemplate.opsForValue().set(seckillProduct.getId().toString(), seckillProduct.getStockNum());
            //redisTemplate.opsForZSet().add("beginSeckill", seckillProduct.getId().toString(), seckillProduct.getStartTIME().getTime());
            // 更新时会覆盖旧值
            redisTemplate.opsForZSet().add("endSeckill", seckillProduct.getId(), seckillProduct.getEndTime().getTime());

            return "true";
        }
    }

    // 删除redis中的开始时间zset和结束时间zset已经存储的数据，等3个
    @Override
    public String deleteSeckillProduct(int seckillProductId) {
        SeckillProduct product = gson.fromJson(redisTemplate.opsForHash().get("seckill", "seckill:" + seckillProductId).toString(), SeckillProduct.class);
        if (product.getStartTIME().compareTo(new Date()) <= 0) {
            return "秒杀图书已经开始售卖，此时删除秒杀图书不合法";
        } else {
            redisTemplate.opsForHash().delete("seckill", "seckill:" + seckillProductId);
            redisTemplate.delete(String.valueOf(seckillProductId));
            seckillProductMapper.deleteById(seckillProductId);
            //redisTemplate.opsForZSet().remove("beginSeckill", seckillProductId);
            redisTemplate.opsForZSet().remove("endSeckill", seckillProductId);
        }
        return "true";
    }

    // 同上更新3个地方的数据
    @Override
    public String updateSeckillProduct(int seckillProductId, Integer stockNum, Double price, Date beginTime, Date endTime) {
        // 若结束时间早于或等于开始时间，则认为该时间设置错误
        // 若beginTime比当前时间还早，则不允许创建该秒杀图书
        if (endTime.compareTo(beginTime) <= 0 || beginTime.before(new Date())) {
            return "秒杀时间设置不合法";
        }
        SeckillProduct seckillProduct = gson.fromJson(redisTemplate.opsForHash().get("seckill", "seckill:" + seckillProductId).toString(), SeckillProduct.class);
        if (seckillProduct.getStartTIME().compareTo(new Date()) <= 0) {
            return "秒杀图书已经开始售卖或结束售卖，此时修改秒杀图书属性不合法";
        }
        seckillProduct.setStockNum(stockNum);
        seckillProduct.setSellingPrice(price);
        seckillProduct.setStartTIME(beginTime);
        seckillProduct.setEndTime(endTime);
        if (seckillProductMapper.updateById(seckillProduct) == 0) {
            return "error";
        } else {
            seckillProductMapper.updateById(seckillProduct);
            // 放入redis，Map中
            redisTemplate.opsForHash().put("seckill", "seckill:" + seckillProduct.getId(), gson.toJson(seckillProduct));
            // redisTemplate.opsForZSet().add("beginSeckill", seckillProduct.getId().toString(), seckillProduct.getStartTIME().getTime());
            if (stockNum != null) {
                redisTemplate.opsForValue().set(seckillProduct.getId().toString(), seckillProduct.getStockNum().toString());
            }
            // 计时器
            if (endTime != null) {
                redisTemplate.opsForZSet().add("endSeckill", seckillProduct.getId(), seckillProduct.getEndTime().getTime());
            }
            return "true";
        }
    }

    // 从redis中获取数据
    // 此处库存为原始库存，不为实际库存
    @Override
    @DS("slave")
    public List<SeckillProduct> listSeckillProducts() {
        List<Object> list = redisTemplate.opsForHash().values("seckill");
        List<SeckillProduct> seckillProductList = new ArrayList<>();
        for (Object object : list) {
            seckillProductList.add(gson.fromJson(object.toString(), SeckillProduct.class));
        }
        return seckillProductList;
    }

    // 从redis中获取数据
    // 此处库存为原始库存，不为实际库存
    @Override
    @DS("slave")
    public SeckillProduct getSeckillProduct(Integer id) {
        Object ss = redisTemplate.opsForHash().get("seckill", "seckill:" + id);
        if (ss == null) {
            return null;
        }
        SeckillProduct product = gson.fromJson(ss.toString(), SeckillProduct.class);
        return product;
    }

    // 用户立即抢购，生成订单
    @Override
    public Long toOrder(Integer userId, int seckillProductId, int num) {
        // 判断该用户是否已经在15秒内抢过该图书
        Object rr = redisTemplate.opsForValue().get(userId + ":" + seckillProductId);
        if (rr != null) {
            return 0L;
        }
        // 过期键设置
        redisTemplate.opsForValue().set(userId + ":" + seckillProductId, 1, 15, TimeUnit.SECONDS);
        // 以下生成订单操作放入MQ中
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setLocation(new ClassPathResource("/seckill.lua"));
        List<String> keys = ImmutableList.of(String.valueOf(seckillProductId));
        Long result = redisTemplate.execute(script, keys, num);
        return result;
    }

    @Override
    @DS("slave")
    public String getCurrentStockNum(Integer id) {
        return redisTemplate.opsForValue().get(id.toString()).toString();
    }

    @Override
    @DS("slave")
    public List<SeckillProduct> listProductsFromDB() {
        return seckillProductMapper.selectList(Wrappers.<SeckillProduct>lambdaQuery().lt(SeckillProduct::getEndTime, new Date()));
    }

    @Override
    public Boolean updateStatus(SeckillProduct product) {
        if (!product.getStartTIME().after(new Date())) {
            return false;
        }
        redisTemplate.opsForHash().put("seckill", "seckill:" + product.getId(), gson.toJson(product));
        return true;
    }

}