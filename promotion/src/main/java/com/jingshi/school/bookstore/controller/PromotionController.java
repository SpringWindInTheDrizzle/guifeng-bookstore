/**
 * FileName: PromotionController
 * Author:   sky
 * Date:     2020/4/10 19:19
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.alibaba.druid.sql.visitor.functions.If;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingshi.school.bookstore.config.ExpirationMessagePostProcessor;
import com.jingshi.school.bookstore.config.QueueConfig;
import com.jingshi.school.bookstore.model.SeckillProductVO;
import com.jingshi.school.bookstore.model.entity.*;
import com.jingshi.school.bookstore.service.AddressService;
import com.jingshi.school.bookstore.service.OrderService;
import com.jingshi.school.bookstore.service.ProductService;
import com.jingshi.school.bookstore.service.SeckillProductService;
import com.jingshi.school.bookstore.util.NumberUtil;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.ResultGenerator;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sky
 * @create 2020/4/10
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/promotion")
public class PromotionController {

    Gson gson = new Gson();

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Reference(version = "1.0.0")
    AddressService addressService;

    @Resource
    SeckillProductService seckillProductService;

    @GetMapping({"/list", "list.html"})
    public String loginPage(HttpServletRequest request) {
        List<SeckillProduct> list = seckillProductService.listSeckillProducts();
        List<SeckillProductVO> dd = new ArrayList<>();
        for (SeckillProduct product : list) {
            product.setStockNum(Integer.valueOf(seckillProductService.getCurrentStockNum(Math.toIntExact(product.getId()))));
            dd.add(new SeckillProductVO(product));
        }
        dd.sort(Comparator.comparing(SeckillProductVO::getStartTIME));
        request.setAttribute("seckills", dd);
        return "seckillList";
    }

    @GetMapping("/update/{id}")
    public String edit(@PathVariable("id") Integer id, HttpServletRequest request) {
        SeckillProduct product = seckillProductService.getSeckillProduct(id);
        if (product == null) {
            return "秒杀图书不存在";
        }
        request.setAttribute("product", new SeckillProductVO(product));
        return "seckillUpdate";
    }

    @GetMapping("/add")
    public String getInsertPage() {
        return "seckillAdd";
    }

    @PostMapping("/del")
    @ResponseBody
    public Result updateState(@RequestParam("promotionId") Integer id) {
        SeckillProduct product = seckillProductService.getSeckillProduct(id);
        if (product == null) {
            return ResultGenerator.genFailResult("无该图书");
        } else if (product.getGoodsSellStatus() == 0) {
            return ResultGenerator.genFailResult("此图书为上架状态，请改为下架状态再删除");
        } else {
            String result = seckillProductService.deleteSeckillProduct(id);
            if (result.equals("true")) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult(result);
            }
        }
    }

    @PostMapping("/update/all")
    @ResponseBody
    public Result updateProduct(@RequestParam("id") Integer id,
                                @RequestParam("price") Double price,
                                @RequestParam("stockNum") Integer num,
                                @RequestParam("time") String time) {
        // 05/05/2020 12:00 AM - 06/10/2020 11:00 PM
        Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy HH:mm").create();
        String d1 = "\"" + time.substring(0, 16) + "\"";
        String d2 = "\"" + time.substring(22, 38) + "\"";
        long tt = 1000 * 60 * 60 * 12;
        Date date1 = gson.fromJson(d1, Date.class);
        Date date2 = gson.fromJson(d2, Date.class);
        if (time.startsWith("PM", 17)) {
            date1 = new Date(date1.getTime() + tt);

        }
        if (time.startsWith("PM", 39)) {
            date2 = new Date(date2.getTime() + tt);
        }
        if (date1.compareTo(date2) >= 0) {
            return ResultGenerator.genFailResult("秒杀结束时间必须晚于秒杀开始时间，请重新设置");
        }
        String result = seckillProductService.updateSeckillProduct(id, num, price, date1, date2);
        if (result.equals("true")) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @PostMapping("/admin/add")
    @ResponseBody
    public Result insertProduct(@RequestParam("productId") Integer productId,
                                @RequestParam("stockNum") Integer stockNum,
                                @RequestParam("price") Double price,
                                @RequestParam("time") String time) {
        // 05/05/2020 12:00 AM - 06/10/2020 11:00 PM
        Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy HH:mm").create();
        String d1 = "\"" + time.substring(0, 16) + "\"";
        String d2 = "\"" + time.substring(22, 38) + "\"";
        long tt = 1000 * 60 * 60 * 12;
        Date date1 = gson.fromJson(d1, Date.class);
        Date date2 = gson.fromJson(d2, Date.class);
        if (time.startsWith("PM", 17)) {
            date1 = new Date(date1.getTime() + tt);
        }
        if (time.startsWith("PM", 39)) {
            date2 = new Date(date2.getTime() + tt);
        }
        if (date1.compareTo(date2) >= 0) {
            return ResultGenerator.genFailResult("秒杀结束时间必须晚于秒杀开始时间，请重新设置");
        }
        String result = seckillProductService.createSeckillProduct(productId, stockNum, price, date1, date2);
        if (result.equals("true")) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @GetMapping("seckill")
    public String dd(HttpServletRequest request) {
        List<SeckillProduct> list = seckillProductService.listSeckillProducts();
        List<SeckillProductVO> dd = new ArrayList<>();
        for (SeckillProduct product : list) {
            // product.setStockNum(Integer.valueOf(seckillProductService.getCurrentStockNum(Math.toIntExact(product.getId()))));
            if (product.getEndTime().before(new Date()) || product.getGoodsSellStatus() == 1) {

            } else {
                dd.add(new SeckillProductVO(product));
            }
        }
        dd.sort(Comparator.comparing(SeckillProductVO::getStartTIME));
        request.setAttribute("products", dd);
        return "seckillShow";
    }

    @GetMapping("/goods/info/{id}")
    @ApiOperation("Get a goods by id")
    public String info(@PathVariable("id") Integer id, HttpServletRequest request) {
        SeckillProduct goods = seckillProductService.getSeckillProduct(id);
        if (goods == null) {
            return "/error";
        } else if (goods.getGoodsSellStatus() == 1) {
            return "/error";
        }
        request.setAttribute("good", new SeckillProductVO(goods));
        return "seckillDetail";
    }

    @PostMapping("/order/{path}")
    @ResponseBody
    public Result toOrder(@RequestParam("id") Integer id,
                          @RequestParam("count") Integer count,
                          HttpServletRequest request,
                          @PathVariable String path) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("用户还未登录，请登录后再进行操作");
        }
        Long userId = Long.valueOf(cookieMap.get("userId").getValue());
        if (!path.equals(redisTemplate.opsForValue().get("ran:" + id).toString())) {
            return ResultGenerator.genFailResult("请点击按钮进行秒杀，动态URL");
        }
        SeckillProduct product1 = seckillProductService.getSeckillProduct(id);
        if (product1 == null) {
            return ResultGenerator.genFailResult("秒杀图书不存在");
        }
        if (product1.getGoodsSellStatus() == 1) {
            return ResultGenerator.genFailResult("该秒杀图书已下架");
        }
        if (product1.getStartTIME().after(new Date())) {
            return ResultGenerator.genFailResult("秒杀还未开始");
        }
        if (product1.getEndTime().before(new Date())) {
            return ResultGenerator.genFailResult("秒杀已结束");
        }
        // cookie中获取
        // Long userId = Long.valueOf(29);
        List<Address> address = addressService.getAddress(Math.toIntExact(userId));
        if (address == null || address.size() == 0) {
            return ResultGenerator.genFailResult("用户未添加地址，请添加地址");
        }
        // 扣减redis库存
        Long type = seckillProductService.toOrder(Math.toIntExact(userId), id, count);
        if (type == 0) {
                return ResultGenerator.genFailResult("用户之前已提交过请求，间隔15秒");
        } else if (type == 2) {
            return ResultGenerator.genFailResult("商品库存不足");
        }
        // 放入消息队列中，供其消费
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("userId", userId);
        map.put("count", count);
        //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("OrderDirectExchange", "OrderDirectRouting", gson.toJson(map));
        return ResultGenerator.genSuccessResult();
    }

    // 每一次都随机
    @GetMapping("/getPath")
    @ResponseBody
    public Result path(@RequestParam("id") Integer id) {
        int num = NumberUtil.genRandomNum(5);
        redisTemplate.opsForValue().set("ran:" + id, num, 5, TimeUnit.SECONDS);
        Result result = new Result();
        result.setData(num);
        result.setResultCode(200);
        result.setMessage("success");
        return result;
    }

    @GetMapping("/finish")
    public String finish(HttpServletRequest request) {
        List<SeckillProduct> list = seckillProductService.listProductsFromDB();
        List<SeckillProductVO> dd = new ArrayList<>();
        for (SeckillProduct product : list) {
            dd.add(new SeckillProductVO(product));
        }
        request.setAttribute("products", dd);
        return "seckillFinish";
    }

    @PostMapping("/admin/status")
    @ResponseBody
    public Result updateState1(@RequestParam("productId") Integer id) {
        SeckillProduct product = seckillProductService.getSeckillProduct(id);
        if (product == null) {
            return ResultGenerator.genFailResult("无该秒杀图书");
        } else {
            int b = product.getGoodsSellStatus();
            b = 1 - b;
            product.setGoodsSellStatus((byte) b);
            if (seckillProductService.updateStatus(product)) {
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult("该秒杀图书正在秒杀中，不允许下架");
            }
        }
    }
}