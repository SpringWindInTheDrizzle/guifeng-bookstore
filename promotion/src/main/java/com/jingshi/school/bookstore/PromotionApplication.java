package com.jingshi.school.bookstore;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.jingshi.school.bookstore.dao")
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@ServletComponentScan
@DubboComponentScan(basePackages = "com.jingshi.school.bookstore.service.impl")
@EnableScheduling
public class PromotionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromotionApplication.class, args);
    }

}
