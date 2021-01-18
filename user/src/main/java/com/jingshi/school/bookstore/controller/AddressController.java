/**
 * FileName: AddressController
 * Author:   sky
 * Date:     2020/4/9 13:57
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.jingshi.school.bookstore.model.entity.Address;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.service.AddressService;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sky
 * @create 2020/4/9
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/user/address")
public class AddressController {

    @Resource
    AddressService addressService;

    @PostMapping("/insert")
    @ResponseBody
    public Result insertAddress(@RequestBody Address address, HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("用户尚未登录，请先登录");
        }
        address.setUserId(Long.valueOf(cookieMap.get("userId").getValue()));
        if (addressService.saveAddress(address).equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("添加地址失败");
        }
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public Result deleteAddress(@RequestParam int addressId) {
        if (addressService.deleteAddress(addressId).equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("删除地址失败");
    }

    @GetMapping("/list")
    @ResponseBody
    public Result listAddress(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (cookieMap.get("userId") == null) {
            return ResultGenerator.genFailResult("用户尚未登录，请先登录");
        }
        Result result = ResultGenerator.genSuccessResult("success");
        request.setAttribute("addresses", addressService.getAddress((Integer.valueOf(cookieMap.get("userId").getValue()))));
        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    public Result updateAddress(@RequestBody Address address) {
        Result result = ResultGenerator.genSuccessResult(addressService.updateAddress(address));
        return result;
    }
}