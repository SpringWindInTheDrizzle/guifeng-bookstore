package com.jingshi.school.bookstore;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@MapperScan("com.jingshi.school.bookstore.dao")
@ServletComponentScan
@DubboComponentScan(basePackages = "com.jingshi.school.bookstore.service.impl")
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

}
