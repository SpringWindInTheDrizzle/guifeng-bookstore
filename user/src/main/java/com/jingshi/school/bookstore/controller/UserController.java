/**
 * FileName: UserController
 * Author:   sky
 * Date:     2020/4/7 15:39
 * Description:
 */
package com.jingshi.school.bookstore.controller;

import com.jingshi.school.bookstore.model.entity.Address;
import com.jingshi.school.bookstore.model.entity.User;
import com.jingshi.school.bookstore.model.enums.ServiceResultEnum;
import com.jingshi.school.bookstore.model.vo.UserVO;
import com.jingshi.school.bookstore.service.AddressService;
import com.jingshi.school.bookstore.service.UserService;
import com.jingshi.school.bookstore.util.PhoneCodeUtil;
import com.jingshi.school.bookstore.util.PhoneUtil;
import com.jingshi.school.bookstore.util.Result;
import com.jingshi.school.bookstore.util.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sky
 * @create 2020/4/7
 * @since 1.0.0
 */
@Slf4j
@CrossOrigin
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    AddressService addressService;

    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "login";
    }

    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "register";
    }

    @GetMapping("/index")
    public String dd() {
        return "index";
    }

    @PostMapping("/login/password")
    @ResponseBody
    public Result login(@RequestParam("loginMessage") String loginName,
                        @RequestParam("password") String password,
                        HttpServletResponse response) {
        // type = 1 以手机方式登录
        // type = 0 以用户名方式登录
        int type = 0;
        // 用户名不允许全数字
        if (PhoneUtil.isPhoneNumber(loginName)) {
            type = 1;
        }
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        User user = userService.login(loginName, password, type);
        //登录成功
        if (user != null) {
            Cookie cookie1 = new Cookie("userId", String.valueOf(user.getId()));
            Cookie cookie2 = new Cookie("userName", user.getUserName());
            cookie1.setMaxAge(24 * 60 * 60);
            cookie2.setMaxAge(24 * 60 * 60);
            cookie1.setPath("/");
            cookie2.setPath("/");
            response.addCookie(cookie1);
            response.addCookie(cookie2);
            return ResultGenerator.genSuccessResult();
        }
        //登录失败
        return ResultGenerator.genFailResult("用户不存在，请确认用户名或手机号");
    }

    @PostMapping("/login/code")
    @ResponseBody
    public Result login(@RequestParam("phoneNumber") String phoneNumber,
                        @RequestParam("code") String code,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        if (StringUtils.isEmpty(phoneNumber)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(code)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (checkPhoneNumberVerify(phoneNumber, code, request)) {
            User user = userService.login(phoneNumber);
            if (user != null) {
                Cookie cookie1 = new Cookie("userId", String.valueOf(user.getId()));
                Cookie cookie2 = new Cookie("userName", user.getUserName());
                cookie1.setMaxAge(24 * 60 * 60);
                cookie2.setMaxAge(24 * 60 * 60);
                cookie1.setPath("/");
                cookie2.setPath("/");
                response.addCookie(cookie1);
                response.addCookie(cookie2);
                return ResultGenerator.genSuccessResult();
            }
        }
        //登录失败
        return ResultGenerator.genFailResult("loginResult");
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie1 = new Cookie("userId", null);
        Cookie cookie2 = new Cookie("userName", null);
        cookie1.setMaxAge(0);
        cookie2.setMaxAge(0);
        cookie1.setPath("/");
        cookie2.setPath("/");
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        // 返回登录视图
        return "login";
    }

    @GetMapping("/info")
    public String info(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        User user = userService.searchUserById(Integer.valueOf(cookieMap.get("userId").getValue()));
        List<Address> addresses = addressService.getAddress(Math.toIntExact(user.getId()));
        if (addresses == null || addresses.size() < 1) {
            Address address = new Address();
            address.setUserId(user.getId());
            address.setAddress("");
            address.setReceiverName("");
            address.setReceiverPhone("");
            request.setAttribute("address", address);
        } else {
            request.setAttribute("address", addresses.get(0));
        }
        request.setAttribute("user", user);
        return "user";
    }

    /**
     * 注册流程
     * 1、输入手机号码，发送验证码，验证
     * 2、输入个人信息并提交
     * 个人信息包含唯一用户名、昵称、性别、密码、重复密码
     * 此方法为提交表单用于注册
     * 密码由前端校验
     *
     * @param loginName
     * @param password
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("phoneNumber") String phoneNumber,
                           @RequestParam("code") String code,
                           @RequestParam("password") String password,
                           HttpServletRequest request) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (PhoneUtil.isPhoneNumber(loginName)) {
            return ResultGenerator.genFailResult("用户名不允许全为数字");
        }
        /*
        if (StringUtils.isEmpty(nickName)) {
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }
         */
        if (!checkPhoneNumberVerify(phoneNumber, code, request)) {
            return ResultGenerator.genFailResult("验证码校验错误");
        }
        User user = new User();
        user.setUserName(loginName);
        user.setNickName(loginName);
        user.setSex(Byte.valueOf((byte) 0));
        user.setPassword(password);
        user.setPhone(phoneNumber);
        String registerResult = userService.register(user);
        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PostMapping("/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody User mallUser) {
        String rr = userService.updateInfo(mallUser);
        Result result;
        if (rr.equals(ServiceResultEnum.SUCCESS.getResult())) {
            //返回成功
            result = ResultGenerator.genSuccessResult();
        } else {
            result = ResultGenerator.genFailResult("修改失败");
        }
        return result;
    }

    @PostMapping("/verify")
    @ResponseBody
    public String phoneNumberVerify(@RequestParam("phoneNumber") String phoneNumber, HttpServletResponse response) {
        // 随机验证码
        String code = PhoneCodeUtil.vcode();
        // 向手机发送验证码
        String result = PhoneCodeUtil.getPhonemsg(phoneNumber, code);
        if (result == "true") {
            // 将验证码存储在Cookie中，不安全，后期修改
            Cookie cookie1 = new Cookie("phoneNumber", phoneNumber);
            Cookie cookie = new Cookie("code", code);
            cookie1.setPath("/");
            cookie.setPath("/");
            cookie.setMaxAge(70);
            cookie1.setMaxAge(70);
            response.addCookie(cookie);
            response.addCookie(cookie1);
        }
        return result;
    }

    private Boolean checkPhoneNumberVerify(String phoneNumber, String code,
                                           HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        if (!phoneNumber.equals(cookieMap.get("phoneNumber").getValue())) {
            return false;
        }
        if (code.equals(cookieMap.get("code").getValue())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 修改用户密码
     *
     * @param phoneNumber
     * @param password
     * @return
     */
    @PostMapping("/user/updatePassword") // TODO: 2020/4/14  
    public Boolean updatePassword(@RequestParam("phoneNumber") String phoneNumber,
                                  @RequestParam("password") String password) {
        return false;
    }

    @GetMapping("/admin")
    public String userAdmin(HttpServletRequest request) {
        List<User> users = userService.listUsers();
        List<UserVO> list = new ArrayList<>();
        for (User user : users) {
            list.add(new UserVO(user));
        }
        request.setAttribute("users", list);
        return "userAdmin";
    }

}